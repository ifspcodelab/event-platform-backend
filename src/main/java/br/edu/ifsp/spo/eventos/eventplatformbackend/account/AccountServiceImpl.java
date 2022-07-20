//package br.edu.ifsp.spo.eventos.eventplatformbackend.account;
//
//import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
//
//public interface AccountServiceImpl {
//
//
//
//    public boolean existsByEmail(String email) {
//        return accountRepository.existsByEmail(email);
//    }
//
//    public Account findByEmail(String email){
//        return accountRepository.findByEmail(email);
//    }
//
//    public Account sendMailPassword(String email) {
//
//        if (existsByEmail(email)) {
//
//            Account account = findByEmail(email);
//
//            //DEVE IMPLEMENTAR TAMBEM O TOKEN
//
//            MailDto email = MailDto.builder()
//                    .title("Redefinição de senha")
//                    .msgHTML(TemplatesHtml.getPasswordReset())
//                    .build();
//
//            Map<String, String> params = CreatorParametersMail.setParameters(
//                    account.getName(),
//                    baseUrl + "/reset-password",
//                    account.getId()
//            );
//            email = FormatterMail.build(email, params);
//            email.setRecipientTo(account.getEmail());
//            email.setReplyTo(replyTo);
//            senderMail.sendEmail(email);
//            return account;
//        }
//    } else {
//        return null;
//    }
//
//    public void changePassword(String email, ResetPasswordDto resetPasswordDto) {
//
//        //FAZER AS VERIFICAÇÕES DO TOKEN
//        if (resetPasswordRepository.findByToken(resetPasswordDto.getToken())) {
//
//            Account account = findByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, email));
//
//            //PASSWORD ENCODER é uma classe que deve ser importada
//            account.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
//            accountRepository.save(account);
//        }
//    } else{
//        //TRATAR AS EXCEÇÕES DO TOKEN
//        return null;
//    }
//}
