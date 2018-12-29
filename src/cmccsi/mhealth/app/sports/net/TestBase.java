package cmccsi.mhealth.app.sports.net;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.RandomStringUtils;

import cmccsi.mhealth.app.sports.basic.AuthUtl;
import cmccsi.mhealth.app.sports.basic.Base64;

public class TestBase {
    public static String salt     = RandomStringUtils.randomAlphanumeric(20);
    public static String password = "1q2w3e4r";
    public static String pass2    = null;
    static {
        try {
            System.out.println(salt);
            String pass1 = calculateUserPassword(AuthUtl.HASH_METHOD_SHA,
                    password);
            System.out.println(pass1);

            pass2 = calculateUserPasswordWithSalt(AuthUtl.HASH_METHOD_SHA,
                    pass1, salt);
            System.out.println(pass2);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getPassWord(String strpwd) {
        System.out.println("salt : " + salt);
        String pass1;
        try {
            pass1 = calculateUserPassword(AuthUtl.HASH_METHOD_SHA, strpwd);
            System.out.println("pass1 : " + pass1);
            pass2 = calculateUserPasswordWithSalt(AuthUtl.HASH_METHOD_SHA,
                    pass1, salt);
            System.out.println("pass2 : " + pass2);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pass2;
    }

    public static String calculateUserPasswordWithSalt(String alg, String pass,
            String salt) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {

        String base64_hash_pass = _Base64Hash(alg, pass.getBytes("utf-8"));
        String data = base64_hash_pass + salt;
        return _Base64Hash(alg, data.getBytes("utf-8"));
    }

    public static String calculateUserPassword(String alg, String pass)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String _alg = alg.toUpperCase();
        String base64_hash_pass = _Base64Hash(_alg, pass.getBytes("utf-8"));
        return "{" + _alg + "}" + base64_hash_pass;
    }

    public static String _Base64Hash(String alg, byte[] data)
            throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance(alg);
        md.reset();
        md.update(data);
        byte[] buff = md.digest();
        return Base64.encodeBytes(buff, true);
    }

}
