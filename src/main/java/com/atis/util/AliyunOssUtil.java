package com.atis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

public class AliyunOssUtil {
	public static String baseDir="test_hd";
	public static String audioDir = "audio";
	public static String imgDir = "img";
	public static String separator="/";
	public static String admin="admin";

	
	public static void  putObject(String bucketName, String key, String filePath) throws FileNotFoundException {
		String accessKeyId = "L1x27POdCvDVpDaJ";
        String accessKeySecret = "KMkRnoNBBV0o7Hz7ifMJme64PYO5qu";
        // 以杭州为例
        String endpoint = "oss-cn-shanghai.aliyuncs.com";
	    // 初始化OSSClient
	    OSSClient client = new OSSClient(endpoint,accessKeyId, accessKeySecret);
	    // 获取指定文件的输入流
	    File file = new File(filePath);
	    InputStream content = new FileInputStream(file);

	    // 创建上传Object的Metadata
	    ObjectMetadata meta = new ObjectMetadata();

	    // 必须设置ContentLength
	    meta.setContentLength(file.length());
        
	    meta.setContentType("audio/mp4");
	    meta.setContentDisposition("inline");
	    // 上传Object.
	    client.putObject(bucketName, key, content, meta);

	}
	   
	   /**
		 * 上传文件到oss
		 * @param
		 * @return 文件的路径
		 */
		public static boolean uploadFile(File file,String userId,String folderName) {
			Map<String,String> map =new HashMap<String,String>();
			if (file != null && file.length()!=0) {
				try {
					String uuid = UUID.randomUUID().toString();
					if(file.exists()){
						file.delete();
					}
					putObject("umsicimg", baseDir+separator+userId+separator+folderName+separator+uuid+file.getName(), file.getPath());
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;

		}

}
