package com.example.demo.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MailService {
	public boolean send(String from, String to, String subject, String content);

	public boolean send(String from, String to, String subject, String content, MultipartFile file);

	public boolean send(String from, String to, String subject, String content, List<MultipartFile> files);

	public boolean sendEmailWithBarcode(String from, String to, String subject, String content, String barcodeBase64);
}
