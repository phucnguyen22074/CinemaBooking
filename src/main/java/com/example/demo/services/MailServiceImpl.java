package com.example.demo.services;

import java.io.File;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.WriterException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
    private BarcodeService barcodeService;

	@Override
	public boolean send(String from, String to, String subject, String content) {
		try {
			MimeMessage mimeMessage = sender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
			sender.send(mimeMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean send(String from, String to, String subject, String content, MultipartFile file) {
		try {
			MimeMessage mimeMessage = sender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
			if (file != null && file.getSize() > 0) {
				messageHelper.addAttachment(file.getOriginalFilename(), file);
			}
			sender.send(mimeMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean send(String from, String to, String subject, String content, List<MultipartFile> files) {
		try {
			MimeMessage mimeMessage = sender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
			if (files != null && !files.isEmpty()) {
				for (MultipartFile file : files) {
					messageHelper.addAttachment(file.getOriginalFilename(), file);
				}
			}
			sender.send(mimeMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailWithBarcode(String from, String to, String subject, String content, String barcode) {
	    try {
	        MimeMessage message = sender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setFrom(from);
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(content, true); // HTML content

	        // Tạo barcode từ BarcodeService
	        byte[] barcodeImage = barcodeService.generateBarcode(barcode);
	        if (barcodeImage == null || barcodeImage.length == 0) {
	            System.out.println("Error: Barcode image is null or empty for barcode: " + barcode);
	            return false;
	        }
	        System.out.println("Barcode image generated, size: " + barcodeImage.length + " bytes");
	        helper.addInline("barcodeImage", new ByteArrayResource(barcodeImage), "image/png");

	        sender.send(message);
	        System.out.println("Email sent successfully to: " + to);
	        return true;
	    } catch (MessagingException | WriterException e) {
	        System.out.println("Failed to send email with barcode: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
	

}
