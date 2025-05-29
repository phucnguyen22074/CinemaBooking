package com.example.demo.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class BarcodeService {

    /**
     * Tạo mã vạch dạng QR Code từ nội dung.
     *
     * @param content Nội dung cần mã hóa (ví dụ: booking ID)
     * @return Mảng byte chứa hình ảnh mã vạch
     */
	public byte[] generateBarcode(String content) throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] barcodeBytes = outputStream.toByteArray();
            System.out.println("Barcode generated for content: " + content + ", size: " + barcodeBytes.length + " bytes");
            return barcodeBytes;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate barcode image", e); // Ném ngoại lệ để xử lý
        }
    }

    /**
     * Lưu mã vạch vào file (tùy chọn).
     *
     * @param content Nội dung cần mã hóa
     * @param filePath Đường dẫn lưu file
     */
    public void saveBarcodeToFile(String content, String filePath) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}