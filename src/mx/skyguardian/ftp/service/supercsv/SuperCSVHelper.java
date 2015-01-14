package mx.skyguardian.ftp.service.supercsv;

import java.io.FileWriter;
import java.util.List;

import mx.skyguardian.ftp.service.bean.Unit;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class SuperCSVHelper {
	private static Logger log = Logger.getLogger(SuperCSVHelper.class);
	private static final CsvPreference PIPE_DELIMITED = new CsvPreference.Builder('"', '|', "\n").build();
	
	private SuperCSVHelper(){
		
	}
	
	public void writeCSVToFile(final List<Unit> units, String fileName) throws Exception {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(
					fileName),
					PIPE_DELIMITED);

			// the header elements are used to map the bean values to each
			// column (names must match)
			final String[] header = new String[] { "Clave Unidad", "Fecha", "Hora", "KM Actual"};
			final String[] fieldMapping = new String[] { "unitId", "date", "time", "currentKm"};
			final CellProcessor[] processors = getProcessors();

			// write the header
			beanWriter.writeHeader(header);

			// write the beans
			for (final Unit unit : units) {
				beanWriter.write(unit, fieldMapping, processors);
			}
			
			log.info("CSV_CREATED>>>Successfully created file [" + fileName + "]");
		} catch (Exception e){
			throw new Exception("Error writing CSV to File. "+ e.getMessage());
		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}
		}
	}

	private CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {
				new UniqueHashCode(), // unitId (must be unique)
				new NotNull(), // fecha
				new NotNull(), // hora
				new NotNull(), // km actual
		};
		return processors;
	}
}
