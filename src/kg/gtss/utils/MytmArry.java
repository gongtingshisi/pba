package kg.gtss.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 信任规则管理类，得到信任规则
 */
class MytmArray implements X509TrustManager {
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {

	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}
};
