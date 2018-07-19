package io.dohack.lottery;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GuessRepositoryTest {

  @Autowired
  private GuessRepository guessRepository;

  @Test
  public void findsClosest() {
    guessRepository.save(new Guess(1L, 35, "hombergs@adesso.de", true));
    guessRepository.save(new Guess(2L, 36, "foo@adesso.de", true));
    guessRepository.save(new Guess(3L, 55, "bar@adesso.de", true));
    List<Guess> winners = guessRepository.findClosestGuesses(35);
    assertThat(winners).hasSize(1);
    assertThat(winners.get(0).getGuessedNumber()).isEqualTo(35);
  }

}