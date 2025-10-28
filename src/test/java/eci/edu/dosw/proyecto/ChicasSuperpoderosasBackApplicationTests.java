package eci.edu.dosw.proyecto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChicasSuperpoderosasBackApplicationTests {
	/**
	 * Test comprensivo por reflexión que recorre las clases del paquete model
	 * e intenta:
	 * - Instanciar (constructor por defecto o constructor con parámetros de muestra)
	 * - Para cada setter público con getter asociado: invocar setter y comprobar getter
	 * - Si la clase sobrescribe equals(Object) comprobar que dos instancias con mismos valores son iguales
	 * - Comprobar que toString() no devuelve null
	 *
	 * Esta prueba está diseñada para cubrir los POJOs del paquete model sin escribir tests manuales
	 * por cada clase; en caso de tipos complejos o constructores no triviales se omiten verificaciones
	 * puntuales y se informa por salida estándar.
	 */
	@Test
	void comprehensiveModelTests() {
		String basePackage = "eci.edu.dosw.proyecto.model";

		// Lista de clases explícita (mantener sincronizada con las clases reales si hay cambios)
		String[] classes = new String[]{
				"eci.edu.dosw.proyecto.model.Faculty",
				"eci.edu.dosw.proyecto.model.Group",
				"eci.edu.dosw.proyecto.model.Dean",
				"eci.edu.dosw.proyecto.model.AuditLog",
				"eci.edu.dosw.proyecto.model.Alert",
				"eci.edu.dosw.proyecto.model.AcademicTrafficLight",
				"eci.edu.dosw.proyecto.model.AcademicPeriodConfig",
				"eci.edu.dosw.proyecto.model.AcademicPeriod",
				"eci.edu.dosw.proyecto.model.SpecialApprovalCase",
				"eci.edu.dosw.proyecto.model.SystemConfig",
				"eci.edu.dosw.proyecto.model.WaitingListEntry",
				"eci.edu.dosw.proyecto.model.WaitingList",
				"eci.edu.dosw.proyecto.model.User",
				"eci.edu.dosw.proyecto.model.SubjectProgress",
				"eci.edu.dosw.proyecto.model.Subject",
				"eci.edu.dosw.proyecto.model.StudyPlanProgress",
				"eci.edu.dosw.proyecto.model.StudentSchedule",
				"eci.edu.dosw.proyecto.model.Schedule",
				"eci.edu.dosw.proyecto.model.RequestHistory",
				"eci.edu.dosw.proyecto.model.Request",
				"eci.edu.dosw.proyecto.model.Report",
				"eci.edu.dosw.proyecto.model.RealTimeStats",
				"eci.edu.dosw.proyecto.model.Program",
				"eci.edu.dosw.proyecto.model.Professor",
				"eci.edu.dosw.proyecto.model.Permission",
				"eci.edu.dosw.proyecto.model.Student",
				"eci.edu.dosw.proyecto.model.Notification",
				"eci.edu.dosw.proyecto.model.ManualAssignment",
				"eci.edu.dosw.proyecto.model.GroupCapacityHistory"
		};

		for (String className : classes) {
			try {
				Class<?> cls = Class.forName(className);

				if (cls.isEnum()) {
					Object[] vals = cls.getEnumConstants();
					System.out.println("Enum " + cls.getSimpleName() + " values=" + (vals == null ? 0 : vals.length));
					continue;
				}

				Object instance = instantiateWithFallback(cls);
				if (instance == null) {
					System.out.println("Omitida instancia de " + className + " (no se pudo crear una instancia)");
					continue;
				}

				// Test setters/getters
				for (java.lang.reflect.Method m : cls.getMethods()) {
					String name = m.getName();
					if (!name.startsWith("set") || m.getParameterCount() != 1) continue;

					String prop = name.substring(3);
					Class<?> paramType = m.getParameterTypes()[0];
					java.lang.reflect.Method getter = findGetter(cls, prop);
					if (getter == null) continue;

					Object sample = sampleValueForType(paramType);
					if (sample == null) {
						// Skip complex/unhandled types
						continue;
					}

					try {
						m.invoke(instance, sample);
						Object got = getter.invoke(instance);
						org.junit.jupiter.api.Assertions.assertEquals(sample, got,
								cls.getSimpleName() + ": propiedad " + prop + " getter/setter mismatch");
					} catch (Exception ex) {
						throw new RuntimeException("Error probando " + cls.getSimpleName() + " property " + prop, ex);
					}
				}

				// Test equals/hashCode if equals está sobrescrito en la clase
				boolean overridesEquals = overridesMethod(cls, "equals", Object.class);
				if (overridesEquals) {
					Object inst2 = instantiateWithFallback(cls);
					if (inst2 != null) {
						// Intentar copiar campos repetidamente mediante setters para hacerlos iguales
						copySettableProperties(cls, instance, inst2);
						boolean eq = instance.equals(inst2);
						org.junit.jupiter.api.Assertions.assertTrue(eq,
								"Se esperaba equals true para dos instancias con los mismos valores en " + cls.getSimpleName());
						org.junit.jupiter.api.Assertions.assertEquals(instance.hashCode(), inst2.hashCode(),
								"hashCode debe ser igual cuando equals es true para " + cls.getSimpleName());
					}
				}

				// toString no debe ser null
				String ts = instance.toString();
				org.junit.jupiter.api.Assertions.assertNotNull(ts, cls.getSimpleName() + " toString() no puede ser null");

			} catch (ClassNotFoundException e) {
				System.out.println("Clase no encontrada: " + className);
			}
		}
	}

	private static boolean overridesMethod(Class<?> cls, String name, Class<?>... params) {
		try {
			java.lang.reflect.Method m = cls.getMethod(name, params);
			return m.getDeclaringClass() != Object.class;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	private static java.lang.reflect.Method findGetter(Class<?> cls, String prop) {
		String g1 = "get" + prop;
		try {
			return cls.getMethod(g1);
		} catch (NoSuchMethodException e) {
			String g2 = "is" + prop;
			try {
				return cls.getMethod(g2);
			} catch (NoSuchMethodException ex) {
				return null;
			}
		}
	}

	private static void copySettableProperties(Class<?> cls, Object from, Object to) {
		for (java.lang.reflect.Method m : cls.getMethods()) {
			String name = m.getName();
			if (!name.startsWith("set") || m.getParameterCount() != 1) continue;
			String prop = name.substring(3);
			java.lang.reflect.Method getter = findGetter(cls, prop);
			if (getter == null) continue;
			try {
				Object value = getter.invoke(from);
				m.invoke(to, value);
			} catch (Exception ignored) {
			}
		}
	}

	private static Object instantiateWithFallback(Class<?> cls) {
		try {
			java.lang.reflect.Constructor<?> noArg = cls.getDeclaredConstructor();
			noArg.setAccessible(true);
			return noArg.newInstance();
		} catch (Exception ignored) {
		}

		// Intentar usar un constructor con parámetros rellenando valores de muestra
		for (java.lang.reflect.Constructor<?> ctor : cls.getDeclaredConstructors()) {
			Class<?>[] pts = ctor.getParameterTypes();
			Object[] args = new Object[pts.length];
			boolean ok = true;
			for (int i = 0; i < pts.length; i++) {
				Object sample = sampleValueForType(pts[i]);
				if (sample == null) {
					ok = false;
					break;
				}
				args[i] = sample;
			}
			if (!ok) continue;
			try {
				ctor.setAccessible(true);
				return ctor.newInstance(args);
			} catch (Exception ignored) {
			}
		}

		return null;
	}

	private static Object sampleValueForType(Class<?> type) {
		if (type.equals(String.class)) return "test-value";
		if (type.equals(Integer.class) || type.equals(int.class)) return 123;
		if (type.equals(Long.class) || type.equals(long.class)) return 123L;
		if (type.equals(Boolean.class) || type.equals(boolean.class)) return true;
		if (type.equals(Double.class) || type.equals(double.class)) return 1.23d;
		if (type.equals(Float.class) || type.equals(float.class)) return 1.2f;
		if (type.equals(java.math.BigDecimal.class)) return java.math.BigDecimal.ONE;
		if (type.equals(java.util.UUID.class)) return java.util.UUID.randomUUID();
		if (type.equals(java.time.LocalDate.class)) return java.time.LocalDate.now();
		if (type.equals(java.time.LocalDateTime.class)) return java.time.LocalDateTime.now();
		if (type.equals(java.util.List.class) || type.equals(java.util.Collection.class)) return java.util.Collections.emptyList();
		if (type.equals(java.util.Set.class)) return java.util.Collections.emptySet();
		if (type.equals(java.util.Map.class)) return java.util.Collections.emptyMap();
		if (type.isEnum()) {
			Object[] cs = type.getEnumConstants();
			return (cs != null && cs.length > 0) ? cs[0] : null;
		}

		// Intentar instanciar recursivamente tipos de model sencillos
		if (type.getPackage() != null && type.getPackage().getName().startsWith("eci.edu.dosw.proyecto.model")) {
			try {
				java.lang.reflect.Constructor<?> c = type.getDeclaredConstructor();
				c.setAccessible(true);
				return c.newInstance();
			} catch (Exception ignored) {
			}
		}

		return null;
	}

}
