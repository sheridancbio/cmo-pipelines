/*
 * Copyright (c) 2016 - 2017 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cbioportal.cmo.pipelines.cvr;
import java.util.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 *
 * @author heinsz
 */
@Configuration
public class EmailUtil {

    @Value("${email.server}")
    private  String server;
    @Value("${email.sender}")
    private  String sender;
    @Value("${email.subject}")
    private  String subject;
    @Value("${email.recipient}")
    private  String recipient;

    private Session session;
    private Properties properties;

    @Bean
    public EmailUtil EmailUtil() {
        return new EmailUtil();
    }

    public EmailUtil() {
        properties = System.getProperties();
        session = Session.getDefaultInstance(properties);
    }

    public  void sendErrorEmail(List<Throwable> exceptions, String parameters) {
        try {
            properties.setProperty("mail.smtp.host", server);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            String body = "An error occured while running the CVR Pipeline with job parameters " + parameters + ".\n\n";
            List<String> messages = new ArrayList<>();
            for (Throwable exception : exceptions) {
                messages.add(ExceptionUtils.getFullStackTrace(exception));
            }
            body += StringUtils.join(messages, "\n\n");
            message.setText(body);
            Transport.send(message);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}