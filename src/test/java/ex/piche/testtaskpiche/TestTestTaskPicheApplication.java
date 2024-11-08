package ex.piche.testtaskpiche;

import org.springframework.boot.SpringApplication;

public class TestTestTaskPicheApplication {

  public static void main(String[] args) {
    SpringApplication.from(TestTaskPicheApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
