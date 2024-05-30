        public FormValidation doSendTestMail(
                @QueryParameter String smtpServer, @QueryParameter String adminAddress, @QueryParameter boolean useSMTPAuth,
                @QueryParameter String smtpAuthUserName, @QueryParameter Secret smtpAuthPasswordSecret,
                @QueryParameter boolean useSsl, @QueryParameter String smtpPort, @QueryParameter String charset,
                @QueryParameter String sendTestMailTo) throws IOException, ServletException, InterruptedException {
            try {
                final Jenkins jenkins = Jenkins.getInstance();
                if (jenkins == null) {
                    throw new IOException("Jenkins instance is not ready");
                }
                
                if (!useSMTPAuth) {
                    smtpAuthUserName = null;
                    smtpAuthPasswordSecret = null;
                }
                
                MimeMessage msg = new MimeMessage(createSession(smtpServer, smtpPort, useSsl, smtpAuthUserName, smtpAuthPasswordSecret));
                msg.setSubject(Messages.Mailer_TestMail_Subject(testEmailCount.incrementAndGet()), charset);
                msg.setText(Messages.Mailer_TestMail_Content(testEmailCount.get(), jenkins.getDisplayName()), charset);
                msg.setFrom(stringToAddress(adminAddress, charset));
                if (StringUtils.isNotBlank(replyToAddress)) {
                    msg.setReplyTo(new Address[]{stringToAddress(replyToAddress, charset)});
                }
                msg.setSentDate(new Date());
                msg.setRecipient(Message.RecipientType.TO, stringToAddress(sendTestMailTo, charset));

                Transport.send(msg);                
                return FormValidation.ok(Messages.Mailer_EmailSentSuccessfully());
            } catch (MessagingException e) {
                return FormValidation.errorWithMarkup("<p>"+Messages.Mailer_FailedToSendEmail()+"</p><pre>"+Util.escape(Functions.printThrowable(e))+"</pre>");
            }
        }