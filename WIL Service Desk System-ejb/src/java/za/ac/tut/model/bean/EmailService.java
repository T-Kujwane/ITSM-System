/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package za.ac.tut.model.bean;

import javax.ejb.Local;
import javax.mail.MessagingException;

@Local
public interface EmailService {
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException;
}
