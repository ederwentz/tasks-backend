package br.ce.wcaquino.taskbackend.utils;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {
	
	@Test
	public void deveRetornarVeTrueParaDatasFuturas() {
		LocalDate date = LocalDate.of(2030, 01, 01);
		Assert.assertFalse(DateUtils.isEqualOrFutureDate(date));
		//System.out.println(DateUtils.isEqualOrFutureDate(date));
	}
	
	@Test
	public void deveRetornarVeFalseParaDatasPassadas() {
		LocalDate date = LocalDate.of(2010, 01, 01);
		Assert.assertFalse(DateUtils.isEqualOrFutureDate(date));
		//System.out.println(DateUtils.isEqualOrFutureDate(date));
	}
	
	@Test
	public void deveRetornarVeTrueParaDatasAtual() {
		LocalDate date = LocalDate.now();
		Assert.assertTrue(DateUtils.isEqualOrFutureDate(date));
		//System.out.println(DateUtils.isEqualOrFutureDate(date));
	}
}