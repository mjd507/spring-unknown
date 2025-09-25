package com.jiandong.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CsvItemProcessor implements ItemProcessor<Person, Person> {

	private final Logger log = LoggerFactory.getLogger(CsvItemProcessor.this.getClass());

	@Override
	public Person process(final Person person) {
		String firstName = person.firstName().toUpperCase();
		String lastName = person.lastName().toUpperCase();

		Person transformedPerson = new Person(firstName, lastName);

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}