package moba.gipger.com.timerservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by vladgerasimenko on 18/04/17.
 */
@SuppressWarnings("MissingPermission")
public class TimerService extends Service {


    private Messenger incomingMessenger = new Messenger(new IncomingHandler(this));
    private Messenger outgoingMessenger;




    // Создаем нужные объекты
    @Override
    public void onCreate() {
        Log.d(TimerService.class.getName(), "onCreate");
        super.onCreate();
    }


    // Чистим ресурсы, потоки ...
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // Возращаем принимающий Handler для TimerServiceManager
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return incomingMessenger.getBinder();
    }



    // Вызывается когда запускаем команду startService
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TimerService.class.getName(), "onStartComand");

        // intent может быть null !!!

        return Service.START_STICKY;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    ///////////////////////

    // Handler который принимает команды от нашего приложения

    private static class IncomingHandler extends Handler {
        private final WeakReference<TimerService> trackingService;

        private IncomingHandler(TimerService timerService) {
            this.trackingService = new WeakReference<>(timerService);
        }

        @Override
        public void handleMessage(Message msg) {
            TimerService service = trackingService.get();
            switch (msg.what) {
                case Messages.HELLO: {
                    // messenger котрый приходит от TimerServiceManager
                    service.setOutgoingMessenger(msg);
                    break;
                }
                case Messages.START_TIMER:
//                    service.startTimer();
                    break;
                case Messages.PAUSE_TIMER:
//                    service.cancelTimer();
                    break;
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void setOutgoingMessenger(Message msg) {
        outgoingMessenger = msg.replyTo;
    }

    private boolean isOutgoingMessengerBound() {
        return outgoingMessenger != null && outgoingMessenger.getBinder().isBinderAlive()
                && outgoingMessenger.getBinder().pingBinder();
    }


    // вызываем метод на Timer tick
    private void replyMessage(int msgType, Bundle bundle) {
        if (!isOutgoingMessengerBound()) return;
        Message msg = Message.obtain(null, msgType, 0, 0);
        msg.setData(bundle);
        try {
            outgoingMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
