package discordInteraction.viewer;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import discordInteraction.Main;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.monsters.MinionMove;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.core.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ViewerMinion extends AbstractFriendlyMonster {
    private static String ID = "Viewer";
    private AbstractMonster target;

    private static String getImageDirectory(Viewer viewer){
        String dir = ConfigUtils.CONFIG_DIR + File.separator + "DiscordInteraction" + File.separator + "CachedImages" + File.separator;
        if (!(new File(dir).exists()))
            new File(dir).mkdirs();
        File imageFile = new File(dir + viewer.getName() + ".png");

        if (!imageFile.exists()) {
            try {
                URL url = new URL(viewer.getAvatarUrl());
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "");
                BufferedImage bImage = ImageIO.read(httpcon.getInputStream());

                Image tmp = bImage.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                BufferedImage dimg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = dimg.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                ImageIO.write(dimg, "png", imageFile);
            } catch (Exception e){
                Main.logger.debug(e);
            }
        }

        if (imageFile.exists())
            return imageFile.getPath();
        else
            return "images/monsters/Viewer.png";
    }

    public ViewerMinion(Viewer viewer, int offsetX, int offsetY) {
        super(viewer.getName(), ID, 35, -3.0F, 10.0F, 5.0F, 5.0F,
                getImageDirectory(viewer),
                offsetX, offsetY);
    }
}
