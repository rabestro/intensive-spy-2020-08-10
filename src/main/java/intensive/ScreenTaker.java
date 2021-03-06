package intensive;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class ScreenTaker extends Thread {
    private static final Logger log = Logger.getLogger(ScreenTaker.class.getName());
    private final AppConfig config;
    private final Robot robot;
    private final ExecutorService uploader;
    private final Rectangle rectangle;

    ScreenTaker(final Robot robot, final AppConfig appConfig) {
        config = appConfig;
        this.robot = robot;
        this.rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        final var poolSize = Runtime.getRuntime().availableProcessors();
        this.uploader = Executors.newFixedThreadPool(poolSize);
        this.setName("Screen Taker");
    }

    @Override
    public void run() {
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::takeScreenshot, 0, config.getInterval(), TimeUnit.MILLISECONDS);
    }

    private void takeScreenshot() {
        final var image = robot.createScreenCapture(rectangle);
        log.fine(() -> "Screenshot taken at " + LocalDateTime.now());

        uploader.submit(new ImageUploader(config, image));
    }

}

