package com.wenyu.hydroelements.hydrostation.dispatchgraph;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.wenyu.hydroelements.operation.basic.HydroDateUtil;

public class DispatchGraphFactory {
	/**
	 * 鏋勯�爄tem
	 * 
	 * @return
	 */
	public static DispatchLineItem createDispatchLineItem(String tbType, LocalDate start, double levelBegin,
			double levelEnd, double output) {
		DispatchLineItem item = new DispatchLineItem();
		item.setBeginTime(start);
		item.setEndTime(HydroDateUtil.calEndTime(start.atTime(0, 0, 0), tbType, 1, 1).toLocalDate().minusDays(1));
		item.setLevelBegin(levelBegin);
		item.setLevelEnd(levelEnd);
		item.setOutput(output);
		return item;
	}

	/**
	 * 鏋勯�爄tem
	 * 
	 * @return
	 */
	public static DispatchLineItem createDispatchLineItem(String tbType, int startPeriod, double levelBegin,
			double levelEnd, double output) {
		DispatchLineItem item = new DispatchLineItem();
		LocalDate start = null;
		if(tbType.equals("MONTH"))
			start = LocalDate.of(LocalDate.now().getYear(), startPeriod, 1);
		else if(tbType.equals("DECAD")){
			start = HydroDateUtil.getLocalDateByYearAndDecad(LocalDate.now().getYear(), startPeriod);
		}
		item.setBeginTime(start);
		item.setEndTime(HydroDateUtil.calEndTime(start.atTime(0, 0, 0), tbType, 1, 1).toLocalDate().minusDays(1));
		item.setLevelBegin(levelBegin);
		item.setLevelEnd(levelEnd);
		item.setOutput(output);
		return item;
	}

	public static DispatchLine createDispatchLine(List<DispatchLineItem> items, int id) {
		DispatchLine line = new DispatchLine();
		line.setId(id);
		String name = getNameById(id);
		line.setName(name);
		line.setOutput(items.get(0).getOutput());
		line.setWaterLevel(items);
		return line;
	}

	/**
	 * @param id
	 * @return 鏍规嵁id锛屽緱鍒板搴旂殑璋冨害绾跨殑鍚嶅瓧
	 */
	private static String getNameById(int id) {
		String name = null;
		int idTemp = id;
		if (idTemp > 100100) {
			idTemp = (int) (idTemp / 10) * 10;
		}
		switch (idTemp) {
		case 100010:
			name = "dispatchgraphWarrantedOutputUpperLine";
			break;
		case 100008:
			name = "dispatchgraphWarrantedOutputUpperLineDelivery";
			break;
		case 100009:
			name = "dispatchgraphWarrantedOutputUpperLineStorage";
			break;
		case 100012:
			name = "dispatchgraphWarrantedOutputLowerLineDelivery";
			break;
		case 100013:
			name = "dispatchgraphWarrantedOutputLowerLineStorage";
			break;
		case 100100:
			name = "dispatchgraphIncreaseOutputLine";
			break;
		case 100110:
			name = "dispatchgraphIncreaseOutputLineDelivery";
			break;
		case 100120:
			name = "dispatchgraphIncreaseOutputLineStorage";
			break;
		case 100200:
			name = "dispatchgraphDecreaseOutputLine";
			break;
		case 100210:
			name = "dispatchgraphDecreaseOutputLineDelivery";
			break;
		case 100220:
			name = "dispatchgraphDecreaseOutputLineStorage";
			break;
		}
		if (!name.contains("Warranted")) {
			name = name + (id - (int) (id / 10) * 10);
		}
		return name;
	}

	public static DispatchLine createDispatchLine(DispatchInputParas input, int lineId, LocalDate start,
			double[] outputLine, double output) {
		List<DispatchLineItem> lineItems = new ArrayList<DispatchLineItem>();
		LocalDate temp = start.plusDays(0);
		for (int i = 0; i < outputLine.length - 1; i++) {
			double levelBegin = outputLine[i];
			double levelEnd = outputLine[i + 1];
			DispatchLineItem item = createDispatchLineItem(input.getTbType(), temp, levelBegin, levelEnd, output);
			lineItems.add(item);
			temp = HydroDateUtil.calEndTime(temp.atStartOfDay(), input.getTbType(), 1, 1).toLocalDate();
		}
		DispatchLine line = createDispatchLine(lineItems, lineId);
		return line;
	}

	
}
