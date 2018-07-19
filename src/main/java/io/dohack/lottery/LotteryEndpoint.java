package io.dohack.lottery;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LotteryEndpoint {

  private GuessRepository guessRepository;

  private JavaMailSender mailSender;

  public LotteryEndpoint(GuessRepository guessRepository, JavaMailSender mailSender) {
	this.guessRepository = guessRepository;
	this.mailSender = mailSender;
  }

  @GetMapping("/guess")
  @Transactional
  public String guessNumber(
		  @RequestParam("number") int number,
		  @RequestParam("email") String email) throws MessagingException {

    if(number < 0 || number > 100){
      return "Number must be between 0 and 100!";
	}

	Guess guess = Guess.builder()
			.guessedNumber(number)
			.email(email)
			.build();

	guessRepository.save(guess);

	sendVerificationMail(email);

	return String.format("Your guess of number %d has been stored. " +
			"Please check your inbox and verify your email address (%s)", number, email);
  }

  private void sendVerificationMail(String email) throws MessagingException {
	MimeMessage message = mailSender.createMimeMessage();
	message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
	message.setContent(String.format("Thanks for taking part in the lottery. " +
			"Please verify your email address by following <a href='http://localhost:8080/verify?email=%s'>this link</a>", email), "text/html");
	mailSender.send(message);
  }

  @GetMapping("/verify")
  public String verifyEmail(
		  @RequestParam("email") String email) {

	Guess guess = guessRepository.findByEmail(email);

	if (guess == null) {
	  return "No Guess with that email address!";
	} else {
	  guess.setEmailVerified(Boolean.TRUE);
	  guessRepository.save(guess);
	  return "Email address verified!";
	}
  }

  @Scheduled(initialDelay = 60000, fixedDelay = 300000)
  public void drawWinners() throws MessagingException {
    log.info("LOTTERY TIME!");
	int winningNumber = new Random().nextInt(101);
	List<Guess> winners = guessRepository.findClosestGuesses(winningNumber);
	for (Guess winner : winners) {
	  sendWinningMail(winner, winningNumber);
	}
	guessRepository.deleteAll();
  }

  private void sendWinningMail(Guess guess, int winningNumber) throws MessagingException {
	MimeMessage message = mailSender.createMimeMessage();
	message.addRecipient(Message.RecipientType.TO, new InternetAddress(guess.getEmail()));
	message.setContent(String.format("You WON! Your guess of %d was closest to %d", guess.getGuessedNumber(), winningNumber), "text/html");
	mailSender.send(message);
  }

}
