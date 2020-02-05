package ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import org.json.simple.JSONObject;

public class connect_server {

    final String ip = "127.0.0.1";
    final String login = "sysadmin";
    final String password = "kfvth20cdn";
    String command = "/usr/bin/sudo /bin/bash /oneproxy/oneproxy.sh";

    public JSONObject GetResult() {
        JSONObject myresult = new JSONObject();
        Channel channel = null;
        Session session = null;
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(login, ip, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            System.out.println("DONE");
            myresult.put("status", "ok");

        } catch (Exception ex) {
            myresult.put("status", "error");
            System.out.println("ex = " + ex);
        } finally {
            channel.disconnect();
            session.disconnect();
        }

        return myresult;
    }
}
