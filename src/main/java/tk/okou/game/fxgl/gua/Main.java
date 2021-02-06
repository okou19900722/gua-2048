package tk.okou.game.fxgl.gua;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {
    public static final int WIDTH = 720 / 2;
    public static final int HEIGHT = 1280 / 2;
    public static final int FLOOR_HEIGHT = 127 / 2;
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("合成大西瓜");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setDeveloperMenuEnabled(true);
    }
    private Entity currentBall;
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("mouse") {
            private Point2D startPoint;
            private double startX;
            /**
             * 等待释放球
             */
            private boolean waitForReleaseBall = false;
            private boolean inShotCD = false;

            @Override
            protected void onActionBegin() {
                if (currentBall == null || this.inShotCD)return;
                this.waitForReleaseBall = true;
                this.startPoint = getInput().getMousePositionWorld();
                this.startX = Main.this.currentBall.getX();
            }
            @Override
            protected void onAction() {
                if (currentBall == null)return;
                Point2D point = getInput().getMousePositionWorld();
                Point2D vec = point.subtract(this.startPoint);
                double newX = this.startX + vec.getX();
                double minX = 0;
                double maxX = WIDTH - currentBall.getWidth();
                if (newX < minX) {
                    currentBall.setX(minX);
                } else if (newX > maxX) {
                    currentBall.setX(maxX);
                } else {
                    currentBall.setX(newX);
                }
                if (point.getX() < 0) {
                    this.startX = this.startPoint.getX();
                } else if (point.getX() > WIDTH) {
                    this.startX = this.startPoint.getX() - currentBall.getWidth();
                }

            }

            @Override
            protected void onActionEnd() {
                if (currentBall == null)return;
//                Point2D point = getInput().getMousePositionWorld();
//                Point2D vec = point.subtract(this.startPoint);
//                currentBall.setX(this.startX + vec.getX());
                this.startPoint = null;

                Ball ball = currentBall.getComponent(Ball.class);
                SpawnData data = new SpawnData(currentBall.getX(), currentBall.getY());
                data.put("id", ball.getNum());
                getGameWorld().spawn("ball2",  data);

                currentBall.removeFromWorld();
                runOnce(() -> {
                    createCurrentBall();
                }, Duration.seconds(0.7));
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initPhysics() {
        FXGL.onCollision(Group.BALL, Group.BALL, (entity1, entity2) -> {
            System.out.println("====");
            Ball ball1 = entity1.getComponent(Ball.class);
            Ball ball2 = entity2.getComponent(Ball.class);
            System.out.println(ball1.getNum() + "=====" + ball2.getNum());
        });
        FXGL.onCollisionBegin(Group.BALL, Group.BALL, (entity1, entity2) -> {
            System.out.println("====");
            Ball ball1 = entity1.getComponent(Ball.class);
            Ball ball2 = entity2.getComponent(Ball.class);
            System.out.println(ball1.getNum() + "=====" + ball2.getNum());
        });
    }


    @Override
    protected void initGame() {
        PhysicsWorld physicsWorld = FXGL.getPhysicsWorld();
        physicsWorld.addCollisionHandler(new CollisionHandler(Group.BALL, Group.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                super.onHitBoxTrigger(a, b, boxA, boxB);
            }
        });

        getGameWorld().addEntityFactory(new Phy());

        //绘制背景
        getGameWorld().spawn("background", 0, 0);
        //绘制地板
        getGameWorld().spawn("floor", 0, HEIGHT - FLOOR_HEIGHT);
        //绘制球
        this.createCurrentBall();

        //设置周围的刚体
        getGameWorld().spawn("wall");
    }

    void createCurrentBall() {
        this.currentBall = getGameWorld().spawn("ball");
        FXGL.animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(1))
                .scale(this.currentBall)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
