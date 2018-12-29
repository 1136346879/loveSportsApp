package cmccsi.mhealth.app.sports.basic;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtl {

	public static final String HASH_METHOD_SHA = "sha";
	public static final String HASH_METHOD_SSHA = "ssha";
	public static final String HASH_METHOD_MD5 = "md5";
	public static final String HASH_METHOD_SMD5 = "smd5";
	public static final String HASH_METHOD_CRYPT = "crypt";

	public static String _Base64Hash(String alg, byte[] data)
			throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance(alg);
		md.reset();
		md.update(data);
		byte[] buff = md.digest();
		return Base64.encodeBytes(buff, true);
	}

	public static String calculateUserPasswordWithSalt(String alg , String pass, String salt)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {

		String base64_hash_pass = _Base64Hash(alg , pass.getBytes("utf-8"));
		String data = base64_hash_pass + salt;
		return _Base64Hash( alg, data.getBytes("utf-8"));
	}
	
	public static String calculateUserPassword(String alg , String pass )
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String _alg = alg.toUpperCase() ;
		String base64_hash_pass = _Base64Hash( _alg , pass.getBytes("utf-8"));
		return "{" + _alg +"}" + base64_hash_pass; 
	}
}
