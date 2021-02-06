package tk.okou.game.fxgl.gua;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.concurrent.ThreadLocalRandom;

public class Phy implements EntityFactory {

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);
        FixtureDef fd = new FixtureDef();
        //密度
        fd.setDensity(0);
        //弹性系数
        fd.setRestitution(0);
        //摩擦系数
        fd.setFriction(0);
        double w = FXGL.getAppWidth();
        double h = FXGL.getAppHeight();
        int thickness = 40;
        return FXGL.entityBuilder()
                .bbox( new HitBox("LEFT",  new Point2D(-thickness, 0.0), BoundingShape.box(thickness, h)))
                .bbox( new HitBox("RIGHT", new Point2D(w, 0.0), BoundingShape.box(thickness, h)))
                .bbox( new HitBox("TOP",   new Point2D(0.0, -thickness), BoundingShape.box(w, thickness)))
                .bbox( new HitBox("BOT",   new Point2D(0.0, h - Main.FLOOR_HEIGHT), BoundingShape.box(w, Main.FLOOR_HEIGHT)))
                .with(physics)
                .build();
    }
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(Group.DEFAULT)
                .viewWithBBox(FXGL.getAssetLoader().loadTexture("background.png", Main.WIDTH, Main.HEIGHT))
                .build();
    }

    @Spawns("floor")
    public Entity newFloor(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(Group.DEFAULT)
                .viewWithBBox(FXGL.getAssetLoader().loadTexture("floor.png", Main.WIDTH, Main.FLOOR_HEIGHT))
                .build();
    }

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        int ball = ThreadLocalRandom.current().nextInt(1, 5);
        return newBallBuilder(ball).build();
    }

    private EntityBuilder newBallBuilder(int id) {
        Texture texture = FXGL.getAssetLoader().loadTexture(id + ".png");
        double newWidth = texture.getWidth() / 2;
        double newHeight = texture.getHeight() / 2;
        texture.setFitWidth(newWidth);
        texture.setFitHeight(newHeight);
        Ball ball = new Ball();
        ball.setNum(id);
        return FXGL.entityBuilder()
                .at((Main.WIDTH - newWidth) / 2, 30)
                .type(Group.BALL)
                .bbox(new HitBox("main", BoundingShape.circle(newWidth / 2)))
                .view(texture)
                .with(ball)
                ;
    }

    @Spawns("ball2")
    public Entity newBall2(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        FixtureDef fd = new FixtureDef();
        //密度
        fd.setDensity(8);
        //弹性系数
        fd.setRestitution(0.2f);
        //摩擦系数
        fd.setFriction(0.01f);

        physics.setOnPhysicsInitialized(() -> {
            Body bd = physics.getBody();
            //线速度衰减系数
            bd.setLinearDamping(0.01f);
            //角速度衰减系数
            bd.setAngularDamping(0.01f);
            bd.setGravityScale(1);
            bd.setFixedRotation(false);
        });


        physics.setFixtureDef(fd);
        int id = data.get("id");
        return newBallBuilder(id)
                .collidable()
                .at(data.getX(), data.getY())
                .with(physics)
                .build();
    }


}
