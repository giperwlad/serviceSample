package moba.gipger.com.timerservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by vladgerasimenko on 18/04/17.
 */

public class TimerServiceManager {

    private static TimerServiceManager instance;
    private Messenger outgoingMessenger;
    private Messenger incomingMessenger;
    private PublishSubject<Long> timerNotifier = PublishSubject.create();


    public static TimerServiceManager getInstance() {
        if (instance == null) {
            synchronized (TimerServiceManager.class) {
                instance = new TimerServiceManager();
            }
        }
        return instance;
    }


    public TimerServiceManager() {
        bindToService();
    }

    private void bindToService() {
        Context context = TimerApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(context, TimerService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_IMPORTANT);
    }

    public void disconnect() {
        Context context = TimerApplication.getInstance().getApplicationContext();
        context.unbindService(connection);
    }


    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            outgoingMessenger = new Messenger(binder);

            incomingMessenger = new Messenger(new IncomingHandler(TimerServiceManager.this));

            Message helloMsg = Message.obtain(null, Messages.HELLO, 0, 0);
            helloMsg.replyTo = incomingMessenger;

            try {
                outgoingMessenger.send(helloMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
           // делаем чтонибудь
        }
    };


    // GETTING OBSERVABLES

    public Observable<Long> getObservableTimer() {
        return timerNotifier;
    }


    private void sendMessageWithId(int msgType) {
        Message msg = Message.obtain(null, msgType, 0, 0);
        sendMessage(msg);
    }

    private void sendMessage(Message msg) {
        try {
            if (outgoingMessenger != null) {
                outgoingMessenger.send(msg);
            }
        } catch (Exception e) {
            // trying to rebind
            bindToService();
        }
    }




    // Отправляем команду сервису запустить таймер
    public void startTimer() {
        Message msg = Message.obtain(null, Messages.START_TIMER, 0, 0);
        sendMessage(msg);
    }


    // Отправляем команду сервису остановить таймер
    public void pauseTimer() {
        Message msg = Message.obtain(null, Messages.PAUSE_TIMER, 0, 0);
        sendMessage(msg);
    }





    // Handler который принимает сообщения от сервиса


    private static class IncomingHandler extends Handler {

        private final WeakReference<TimerServiceManager> manager;

        private IncomingHandler(TimerServiceManager manager) {
            this.manager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            TimerServiceManager a = manager.get();
            if (a == null) return;
            switch (msg.what) {
                case Messages.SECONDS:
                    a.timerNotifier.onNext(msg.getData().getLong(TimerBundle.SECONDS));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
