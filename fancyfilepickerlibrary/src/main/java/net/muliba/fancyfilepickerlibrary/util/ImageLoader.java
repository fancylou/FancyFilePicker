package net.muliba.fancyfilepickerlibrary.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by FancyLou on 2016/3/15.
 */
public class ImageLoader {

    private static ImageLoader mInstance;
    private static final int DEFAULT_THREAD_COUNT = 1;

    /**
     * 图片核心缓存对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    /**
     * 队列调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    private Semaphore mPoolThreadHandlerSemaphore = new Semaphore(0);

    private Semaphore mSemaphoreThread;
    /**
     * UI使用的handler
     */
    private Handler mUIHandler;


    public enum  Type {
        LIFO, FIFO
    }


    private ImageLoader(int mThreadCount, Type mType) {
        init(mThreadCount, mType);
    }

    private void init(int mThreadCount, Type mType) {
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThread.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                //释放一个信号量
                mPoolThreadHandlerSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        /**
         * 初始化cache对象
         */
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取应用的最大内存
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mTaskQueue = new LinkedList<>();
        this.mType = mType;
        mSemaphoreThread = new Semaphore(mThreadCount);
    }




    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }


    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获取图片， 设置给imageview
                    ImgBean bean = (ImgBean) msg.obj;
                    ImageView imageView1 = bean.getImageView();
                    String path = bean.getPath();
                    Bitmap bitmap = bean.getBitmap();
                    if (imageView1.getTag().toString().equals(path)) {
                        imageView1.setImageBitmap(bitmap);
                    }
                }
            };
        }
        Bitmap bitmap = getBitmapFromLruCache(path);
        if (bitmap!=null) {
            refreshBitmap(bitmap, path, imageView);
        }else {
            addTask(new Runnable(){

                @Override
                public void run() {
                    //加载图片
                    //1 获取图片要显示的大小
                    ImageSize size = getImageSize(imageView);
                    //2 压缩图片
                    Bitmap bitmap1 = decodeSampleBitmapFromPath(path, size.width, size.height);
                    //3 将图片加入到缓存
                    addBitmap2LruCache(path, bitmap1);
                    //4 加载图片
                    refreshBitmap(bitmap1, path, imageView);

                    mSemaphoreThread.release();
                }
            });
        }
    }

    private void refreshBitmap(Bitmap bitmap1, String path, ImageView imageView) {
        Message message = Message.obtain();
        ImgBean bean = new ImgBean(bitmap1, path, imageView);
        message.obj = bean;
        mUIHandler.sendMessage(message);
    }

    /**
     * 图片加入到缓存
     * @param path
     * @param bitmap1
     */
    private void addBitmap2LruCache(String path, Bitmap bitmap1) {
        if (getBitmapFromLruCache(path) == null ) {
            if (bitmap1 != null) {
                mLruCache.put(path, bitmap1);
            }
        }
    }

    /**
     * 压缩图片 根据传入的宽和高
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampleBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateSampleSize(options, width, height);
        //开始压缩图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 计算SampleSize
     * @param options
     * @param width
     * @param height
     * @return
     */
    private int calculateSampleSize(BitmapFactory.Options options, int width, int height) {
        int oWidth = options.outWidth;
        int oHeight = options.outHeight;
        int inSampleSize = 1;
        if (oWidth > width || oHeight > height) {
            int widthRadio = Math.round(oWidth*1.0f / width);
            int heightRadio = Math.round(oHeight * 1.0f / height);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 根据ImageView 获取一个适当的压缩的高和宽
     * @param imageView
     * @return
     */
    private ImageSize getImageSize(ImageView imageView) {
        ImageSize size = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = layoutParams.width;
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <=0 ) {
            width = displayMetrics.widthPixels;
        }
        int height = imageView.getHeight();
        if (height <= 0) {
            height = layoutParams.height;
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <=0 ) {
            height = displayMetrics.heightPixels;
        }
        size.width = width;
        size.height = height;
        return size;
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        Field field = null;
        try {
            field = ImageView.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue <Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHandler==null){
                mPoolThreadHandlerSemaphore.acquire();
            }
        } catch (InterruptedException e) {
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Runnable getTask() {
        if (mType == Type.FIFO) {//first in first out
            return  mTaskQueue.removeFirst();
        }else if(mType == Type.LIFO) {// last in first out
            return mTaskQueue.removeLast();
        }
        return null;
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }


    public class ImageSize {
        int width;
        int height;
    }

    public class ImgBean {
        Bitmap bitmap;
        String path;
        ImageView imageView;

        public ImgBean(Bitmap bitmap, String path, ImageView imageView) {
            this.bitmap = bitmap;
            this.path = path;
            this.imageView = imageView;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
