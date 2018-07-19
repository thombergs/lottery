package io.dohack.lottery;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GuessRepository extends CrudRepository<Guess, Long> {

  Guess findByEmail(String email);

  @Query("select g from Guess g where g.emailVerified=true and abs(g.guessedNumber - :winningNumber) = (select min(abs(g2.guessedNumber - :winningNumber)) from Guess g2 where g2.emailVerified=true)")
  List<Guess> findClosestGuesses(@Param("winningNumber") int winningNumber);
}
