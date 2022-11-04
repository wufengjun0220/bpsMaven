package com.mingtech.utils;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.json.JSONObject;

public class HttpUtil {
	
	private static Log log = LogFactory.getLog(HttpUtil.class);
	private static RequestConfig defaultRequestConfig = RequestConfig.custom()
		    .setSocketTimeout(50000)
		    .setConnectTimeout(50000)
		    .setConnectionRequestTimeout(50000)
		    .build();
	
	/**
	 * http post client
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static String httpGet(String url, Map<String, String> params)  {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		  CloseableHttpResponse closeableHttpResponse  = null;
		  HttpGet httpGet = null;
		  BufferedReader br = null;
		  InputStreamReader is = null;
		try {
			//建立httpPost
			httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				    .build();
			httpGet.setConfig(requestConfig);
			
	        //加入参数
			List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
	        if (params != null) {
	        	for (Map.Entry<String, String> entry : params.entrySet()) {
	        		NameValuePair nmValue = new BasicNameValuePair(entry.getKey(), entry.getValue());
	    			nameValueList.add(nmValue);
	        	}
	        }
	        if(!nameValueList.isEmpty()){
	        	URIBuilder uriBuilder = new URIBuilder(URI.create(url));
				uriBuilder.addParameters(nameValueList);
				httpGet.setURI(uriBuilder.setCharset(CharsetUtils.get("UTF-8")).build());
	        }
	        log.info("http get 发送內容：   " + nameValueList.toString());
	        //发起请求
	        //httpclient.
	         closeableHttpResponse = httpclient.execute(httpGet);
		
	        //获取请求返回
	        HttpEntity entity = closeableHttpResponse.getEntity();
	        
	        //获取编码标识
	        String contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
            String contentType2 = contentType != null ? (contentType.split(";").length > 1 ? contentType.split(";")[1].trim() : "") : "";
            String charset = contentType2.split("=").length > 1 ? contentType2.split("=")[1] : "UTF-8";
            is = new InputStreamReader(entity.getContent(),charset);
            //解析返回内容
	        br = new BufferedReader(is);
	        StringBuilder sb = new StringBuilder();
	        String len =null;
	        while ((len = br.readLine()) != null) {
    			sb.append(len);
    		}

    		log.info("http返回內容： " + sb);
    		//ת为json格式
    		return sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					log.error("关闭流BufferedReader出错",e);
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					log.error("关闭流InputStreamReader出错",e);
				}
			}
			
			  if (closeableHttpResponse != null) {
				  try {
					closeableHttpResponse.close();
				} catch (IOException e) {
					log.error("关闭HttpResponse:",e);
				}
	            }
            if (httpGet != null) {
            	httpGet.releaseConnection();
            }
            if (httpclient != null) {
                try {
                	httpclient.close();
                } catch (IOException e) {
                	log.error("关闭httpclient:",e);
                }
            }
        }
		return "{RespCode:'9999',RespDesc:'通讯异常'}";
	}
	
	
	/**
	 * http post client
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static String httpPost(String url, Map<String, String> params)  {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		  CloseableHttpResponse closeableHttpResponse  = null;
		  HttpPost httpPost = null;
		  InputStreamReader is = null;
		  BufferedReader br = null;
		try {
			//建立httpPost
			 httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				    .build();
			httpPost.setConfig(requestConfig);
	        //加入参数
	        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>(); 
	        if (params != null) {
	        	for (Map.Entry<String, String> entry : params.entrySet()) {
	        		formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	        	}
	        }
	        httpPost.setEntity(new UrlEncodedFormEntity(formparams ,"UTF-8"));  
	        
	        log.info("http发送內容：   " + formparams);
	        //发起请求
	        //httpclient.
	         closeableHttpResponse = httpclient.execute(httpPost);
		
	        //获取请求返回
	        HttpEntity entity = closeableHttpResponse.getEntity();
	        
	        //获取编码标识
	        String contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
            String contentType2 = contentType != null ? (contentType.split(";").length > 1 ? contentType.split(";")[1].trim() : "") : "";
            String charset = contentType2.split("=").length > 1 ? contentType2.split("=")[1] : "UTF-8";
            is = new InputStreamReader(entity.getContent(),charset);
            //解析返回内容
	        br = new BufferedReader(is);
	        StringBuilder sb = new StringBuilder();
	        String len =null;
	        while ((len = br.readLine()) != null) {
    			sb.append(len);
    		}

    		log.info("http返回內容： " + sb);
    		//ת为json格式
    		return sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					log.error("关闭BufferedReader异常:",e);
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					log.error("关闭InputStreamReader异常:",e);
				}
			}
			  if (closeableHttpResponse != null) {
				  try {
					closeableHttpResponse.close();
				} catch (IOException e) {
					log.error("closeableHttpResponse:",e);
				}
	            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpclient != null) {
                try {
                	httpclient.close();
                } catch (IOException e) {
                	log.error("httpclient:",e);
                }
            }
        }
		return "{RespCode:'9999',RespDesc:'通讯异常'}";
	}
	
	
	/**
	 * http post client
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static String sendJsonByPost(String url, Map<String, String> params)  {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		  CloseableHttpResponse closeableHttpResponse  = null;
		  HttpPost httpPost = null;
		  InputStreamReader is = null;
		  BufferedReader br = null;
		try {
			//建立httpPost
			 httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				    .build();
			httpPost.setConfig(requestConfig);
	        //加入参数
			
			JSONObject jsonParams = new JSONObject();
	        if (params != null) {
	        	for (Map.Entry<String, String> entry : params.entrySet()) {
	        		jsonParams.put(entry.getKey(), entry.getValue());
	        	}
	        }
	        httpPost.setEntity(new StringEntity(jsonParams.toString(), Charset.forName("UTF-8")));  
	        
	        log.info("http发送內容：   " + jsonParams);
	        //发起请求
	        //httpclient.
	         closeableHttpResponse = httpclient.execute(httpPost);
		
	        //获取请求返回
	        HttpEntity entity = closeableHttpResponse.getEntity();
	        
	        //获取编码标识
	        String contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
            String contentType2 = contentType != null ? (contentType.split(";").length > 1 ? contentType.split(";")[1].trim() : "") : "";
            String charset = contentType2.split("=").length > 1 ? contentType2.split("=")[1] : "UTF-8";
            is = new InputStreamReader(entity.getContent(),charset);
            //解析返回内容
	        br = new BufferedReader(is);
	        StringBuilder sb = new StringBuilder();
	        String len =null;
	        while ((len = br.readLine()) != null) {
    			sb.append(len);
    		}

    		log.info("http返回內容： " + sb);
    		//ת为json格式
    		String back = sb.toString().replace("\"{", "{").replace("}\"", "}");
    		log.info("返回json内容：" + back);
    		return back;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					log.error("关闭BufferedReader异常:",e);
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					log.error("关闭InputStreamReader异常:",e);
				}
			}
			  if (closeableHttpResponse != null) {
				  try {
					closeableHttpResponse.close();
				} catch (IOException e) {
					log.error("closeableHttpResponse:",e);
				}
	            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpclient != null) {
                try {
                	httpclient.close();
                } catch (IOException e) {
                	log.error("httpclient:",e);
                }
            }
        }
		return "{RespCode:'9999',RespDesc:'通讯异常'}";
	}
	
	
	/**
	 * http post client
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param requestConfig http请求配置
	 * @return
	 * @throws Exception 
	 */
	public static String httpPost(String url, Map<String, String> params,RequestConfig requestConfig)  {
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		  CloseableHttpResponse closeableHttpResponse  = null;
		  HttpPost httpPost = null;
		  InputStreamReader is = null;
		  BufferedReader br = null;
		try {
			//建立httpPost
			 httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
	        //加入参数
	        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>(); 
	        if (params != null) {
	        	for (Map.Entry<String, String> entry : params.entrySet()) {
	        		formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	        	}
	        }
	        httpPost.setEntity(new UrlEncodedFormEntity(formparams ,"UTF-8"));  
	        log.info("http发送內容：   " + formparams);
	        //发起请求
	        //httpclient.
	         closeableHttpResponse = httpclient.execute(httpPost);
	        //获取请求返回
	        HttpEntity entity = closeableHttpResponse.getEntity();
	        //获取编码标识
	        String contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
            String contentType2 = contentType != null ? (contentType.split(";").length > 1 ? contentType.split(";")[1].trim() : "") : "";
            String charset = contentType2.split("=").length > 1 ? contentType2.split("=")[1] : "UTF-8";
            is = new InputStreamReader(entity.getContent(),charset);
            //解析返回内容
	        br = new BufferedReader(is);
	        StringBuilder sb = new StringBuilder();
	        String len =null;
	        while ((len = br.readLine()) != null) {
    			sb.append(len);
    		}
    		log.info("http返回內容： " + sb);
    		//ת为json格式
    		return sb.toString();
		} catch (UnknownHostException e){
			log.error(e.getMessage(), e);
			return "-1";
		}catch ( ConnectTimeoutException e){
			log.error(e.getMessage(), e);
			return "-2";
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return "-3";
		}finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					log.error("关闭BufferedReader异常:",e);
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					log.error("关闭InputStreamReader异常:",e);
				}
			}
			  if (closeableHttpResponse != null) {
				  try {
					closeableHttpResponse.close();
				} catch (IOException e) {
					log.error("closeableHttpResponse:",e);
				}
	            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpclient != null) {
                try {
                	httpclient.close();
                } catch (IOException e) {
                	log.error("httpclient:",e);
                }
            }
        }
	}
	
	
	/**
	 * http post client
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static String httpPut(String url, Map<String, String> params)  {
		
	        CloseableHttpClient httpClient = HttpClients.createDefault();     
	         
	        CloseableHttpResponse  httpResponse = null;  
	        HttpPut httpPut = null;
			BufferedReader br = null;
			InputStreamReader is = null;
	        try {    
	        	httpPut = new HttpPut(url);  
		        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
					    .build();
		        httpPut.setConfig(requestConfig);  
		        //加入参数
		        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>(); 
		        if (params != null) {
		        	for (Map.Entry<String, String> entry : params.entrySet()) {
		        		formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		        	}
		        }
		        httpPut.setEntity(new UrlEncodedFormEntity(formparams ,"UTF-8")); 
	            //响应信息  
	            httpResponse = httpClient.execute(httpPut);    
	            HttpEntity entity = httpResponse.getEntity();    
	            //获取编码标识
	            String contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
	            String contentType2 = contentType != null ? (contentType.split(";").length > 1 ? contentType.split(";")[1].trim() : "") : "";
	            String charset = contentType2.split("=").length > 1 ? contentType2.split("=")[1] : "UTF-8";
	            is = new InputStreamReader(entity.getContent(),charset);
	            //解析返回内容
		        br = new BufferedReader(is);
		        StringBuilder sb = new StringBuilder();
		        String len =null;
		        while ((len = br.readLine()) != null) {
	    			sb.append(len);
	    		}

	    		//ת为json格式
	    		return sb.toString(); 
	        } catch (Exception e) {    
	            log.error("业务处理异常",e);    
	        }finally{    
	        	if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						log.error("关闭BufferedReader异常:",e);
					}
				}
	        	if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						log.error("关闭InputStreamReader异常:",e);
					}
				}
				if (httpResponse != null) {
					try {
						httpResponse.close();
					} catch (IOException e) {
						log.error("httpResponse:",e);
					}
		        }
	            if (httpPut != null) {
	            	httpPut.releaseConnection();
	            }
	            if (httpClient != null) {
	                try {
	                	httpClient.close();
	                } catch (IOException e) {
	                	log.error("httpClient:",e);
	                }
	            }
	        }    
	       
	        return "{RespCode:'9999',RespDesc:'通讯异常'}";
	}
}
