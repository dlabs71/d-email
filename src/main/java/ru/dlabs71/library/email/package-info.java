/**
 * D-email is the library that provides a simple opportunity to send and receive email messages.
 * It had been built to simplify the lives of developers who needed to quickly connect to an email server and send
 * or receive email messages. Thus, this library provides two main classes that implement the facade pattern.
 *
 * <p>There are two facades:
 * <ul>
 *     <li>DEmailSender - for sending email message (using SMTP protocol)</li>
 *     <li>DEmailReceiver - for receiving email message (using IMAP protocol)</li>
 * </ul>
 *
 * <p>Common mechanisms of this library bases on two library:
 * <ul>
 *     <li>jakarta.mail:jakarta.mail-api</li>
 *     <li>org.eclipse.angus:angus-mail</li>
 * </ul>
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-11-02</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */

package ru.dlabs71.library.email;