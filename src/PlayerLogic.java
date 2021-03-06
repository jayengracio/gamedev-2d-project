import entity.Player;
import util.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerLogic {
    private int i = 0;
    private int lvl, lvl2, lvl3  = 0;

    // Hazard/environmental objects collision against a player
    public void hazardCollision(CopyOnWriteArrayList<GameObject> HazardList, Player Player) {
        if (Player.getLives() > 0) {
            for (GameObject temp : HazardList) {
                if (Math.abs(temp.getCentre().getX() - Player.getCentre().getX()) < temp.getWidth()
                        && Math.abs(temp.getCentre().getY() - Player.getCentre().getY()) < temp.getHeight() && !Player.isInvincible()) {
                    HazardList.remove(temp);
                    Player.setLives(Player.getLives() - 1);

                    SoundEffect sfx = new SoundEffect("sfx/sfx_lose.wav");
                    sfx.playSFX();

                    Player.setInvincible(true);

                    // show visual damage
                    Player.setTexture("res/playerDamaged.png");
                    Timer timer = new Timer();
                    //ShowDamage damage = new ShowDamage(Player, "res/playerShip1.png");
                    TimerTask damage = new TimerTask() {
                        @Override
                        public void run() {
                            i++;
                            if (i >= 2) {
                                Player.setTexture(Player.getDefaultTexture());
                                Player.setInvincible(false);
                                i = 0;
                                cancel();
                            }
                        }
                    };
                    timer.schedule(damage, 300, 1000);
                }
            }
        }
    }

    // Enemy bullets collision against a player
    public void bulletCollision(CopyOnWriteArrayList<GameObject> EnemyBulletList, Player Player) {
        if (Player.getLives() > 0) {
            for (GameObject Bullet : EnemyBulletList) {
                if (Math.abs(Player.getCentre().getX() - Bullet.getCentre().getX()) < Player.getWidth()
                        && Math.abs(Player.getCentre().getY() - Bullet.getCentre().getY()) < Player.getHeight() && !Player.isInvincible()) {
                    EnemyBulletList.remove(Bullet);
                    Player.setLives(Player.getLives() - 1);

                    SoundEffect sfx = new SoundEffect("sfx/sfx_lose.wav");
                    sfx.playSFX();

                    Player.setInvincible(true);

                    // show visual damage
                    Player.setTexture("res/playerDamaged.png");
                    Timer timer = new Timer();
                    //ShowDamage damage = new ShowDamage(Player, "res/playerShip1.png");
                    TimerTask damage = new TimerTask() {
                        @Override
                        public void run() {
                            i++;
                            if (i >= 2) {
                                Player.setTexture(Player.getDefaultTexture());
                                Player.setInvincible(false);
                                i = 0;
                                cancel();
                            }
                        }
                    };
                    timer.schedule(damage, 300, 1000);
                    EnemyBulletList.remove(Bullet);
                }
            }
        }
    }

    // Control logic for player
    public void playerLogic(Model model, Player Player, Controller controller, CopyOnWriteArrayList<GameObject> BulletList, String bulletTexture) {
        // smoother animation is possible if we make a target position  // done but may try to change things for students
        // check for movement and if you fired a bullet
        if (controller.isKeyAPressed()) {
            Player.getCentre().ApplyVector(new Vector3f((float) -1.3, 0, 0));
        }

        if (controller.isKeyDPressed()) {
            Player.getCentre().ApplyVector(new Vector3f((float) 1.3, 0, 0));
        }

        if (controller.isKeyWPressed()) {
            Player.getCentre().ApplyVector(new Vector3f(0, (float) 1.3, 0));
        }

        if (controller.isKeySPressed()) {
            Player.getCentre().ApplyVector(new Vector3f(0, (float) -1.3, 0));
        }

        // Fire bullet
        if (controller.isKeySpacePressed()) {
            if (Player.getAmmo() == 0) {
                Player.setAmmo(-1);
                Timer timer = new Timer();
                ReloadAmmo reload = new ReloadAmmo(Player, Player.getMaxAmmo());
                timer.schedule(reload, 1000, 1000);
            } else if (Player.getAmmo() == -1) {
                // Does nothing. This stops timer from scheduling the same task multiple times
            } else {
                CreateBullet(BulletList, Player, bulletTexture);
                Player.setAmmo(Player.getAmmo() - 1);
            }
            controller.setKeySpacePressed(false);
        }

        // Enable shield
        if (controller.isKeyCPressed() && Player.getUpgradeLevel() >= 2 && controller.getTemp() == 0) {
            controller.setTemp(1);
            SoundEffect sfx = new SoundEffect("sfx/sfx_shieldUp.wav");
            sfx.playSFX();

            Player.setInvincible(true);
            Player.setTexture("res/player_shieldUp.png");

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Player.setShieldCd(true);
                    controller.setShieldTime(controller.getShieldTime() + 1);
                    if (controller.getShieldTime() == 20) {
                        controller.setTemp(0);
                        controller.setShieldTime(0);
                        Player.setShieldCd(false);
                        timer.cancel();
                        timer.purge();
                    } else if (controller.getShieldTime() == 5) {
                        Player.setTexture(Player.getDefaultTexture());
                        Player.setInvincible(false);
                        SoundEffect sfx = new SoundEffect("sfx/sfx_shieldDown.wav");
                        sfx.playSFX();
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }

        if (Player.getCentre().getX() == 0.0f) {
            Player.getCentre().setX(900);
        } else if (Player.getCentre().getX() == 900.0f) {
            Player.getCentre().setX(0);
        }

        // Player upgrades when reaching certain score level
        switch(model.getScore()) {
            case 30:
                Player.setUpgradeLevel(2);
                if (lvl == 0) {
                    SoundEffect sfx = new SoundEffect("sfx/level_up.wav");
                    sfx.playSFX();
                    lvl = 1;
                }
                break;
            case 70:
                Player.setUpgradeLevel(3);
                if (lvl2 == 0) {
                    SoundEffect sfx = new SoundEffect("sfx/level_up.wav");
                    sfx.playSFX();
                    lvl2 = 1;
                }
                break;
            case 130:
                Player.setUpgradeLevel(4);
                if (lvl3 == 0) {
                    SoundEffect sfx = new SoundEffect("sfx/level_up.wav");
                    sfx.playSFX();
                    lvl3 = 1;
                }
                break;
            default:
                // nothing
        }

        if (Player.getLives() <= 0) {
            Player.setDead(true);
        }
    }

    // Player's bullet logic
    public void playerBulletLogic(CopyOnWriteArrayList<GameObject> BulletList, Player Player) {
        // move bullets
        for (GameObject temp : BulletList) {

            //check to move them
            if (Player.getUpgradeLevel() >= 3) {
                temp.getCentre().ApplyVector(new Vector3f(0, 3.2f, 0));
            } else temp.getCentre().ApplyVector(new Vector3f(0, 2.2f, 0));

            //see if they hit anything
            //see if they get to the top of the screen ( remember 0 is the top
            if (temp.getCentre().getY() == 0) {
                BulletList.remove(temp);
            }
        }
    }

    // Create bullet for player
    private void CreateBullet(CopyOnWriteArrayList<GameObject> BulletList, Player Player, String texture) {
        BulletList.add(new GameObject(texture, 9, 33, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f)));

        if (Player.getUpgradeLevel() >= 3)
            BulletList.add(new GameObject(texture, 9, 33, new Point3f(Player.getCentre().getX() + 60, Player.getCentre().getY(), 0.0f)));

        SoundEffect sfx = new SoundEffect("sfx/sfx_laser1.wav");
        sfx.playSFX();
    }
}
