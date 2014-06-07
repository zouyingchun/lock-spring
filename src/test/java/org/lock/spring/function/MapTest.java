package org.lock.spring.function;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MapTest {

	@Test
	public void testMapGet() {
		Map<TestA, Integer> map = new HashMap<TestA, Integer>();
		map.put(new TestA(2), 2);
		System.out.println(map.get(new TestA(2)));
	}

	static class TestA {
		private Integer a;

		public TestA(Integer a) {
			super();
			this.a = a;
		}

		/**
		 * @return the {@link #a}
		 */
		public Integer getA() {
			return a;
		}

		/**
		 * @param a
		 *                the {@link #a} to set
		 */
		public void setA(Integer a) {
			this.a = a;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			Integer originInteger = (Integer) obj;
			return this.a.intValue() == originInteger.intValue();
		}

	}

}
