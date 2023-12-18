package com.example.application;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private Map<String, String> mParams;
    private Map<String, DataPart> mByteData;

    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<NetworkResponse> listener) {
        super(Method.POST, url, errorListener);
        this.mErrorListener = errorListener;
        this.mListener = listener;
        mParams = new HashMap<>();
        mByteData = new HashMap<>();
        setShouldCache(false);
    }

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    public void setByteData(Map<String, DataPart> data) {
        mByteData = data;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;";
    }

    @Override
    public byte[] getBody() {
        DataOutputStream dos;
        ByteArrayOutputStream baos;

        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);

            // 파라미터 추가
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                buildTextPart(dos, entry.getKey(), entry.getValue());
            }

            // 이미지 추가
            for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                buildDataPart(dos, entry.getValue(), entry.getKey());
            }

            dos.writeBytes("\r\n");

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String key, String value) throws IOException {
        dataOutputStream.writeBytes("\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + "\r\n");
        dataOutputStream.writeBytes("\r\n");
        dataOutputStream.writeBytes(value + "\r\n");
    }

    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes("\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" +
                dataFile.fileName + "\"" + "\r\n");
        if (dataFile.type != null && !dataFile.type.trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.type + "\r\n");
        }
        dataOutputStream.writeBytes("\r\n");

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.content);
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes("\r\n");
    }


    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public class DataPart {
        public String fileName;
        public byte[] content;
        public String type;

        public DataPart(String name, byte[] data) {
            fileName = name;
            content = data;
        }
    }
}

