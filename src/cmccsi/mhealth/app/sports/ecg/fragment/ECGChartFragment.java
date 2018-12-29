package cmccsi.mhealth.app.sports.ecg.fragment;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.views.StaticSplineChartView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.common.MathUtil;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.ecg.activity.ECGDetailChartActivity;
import cmccsi.mhealth.app.sports.ecg.utils.ECGDataFilter;
import cmccsi.mhealth.app.sports.ecg.utils.Range;
import cmccsi.mhealth.app.sports.ecg.utils.RangeUtil;
import cmccsi.mhealth.app.sports.R;

/**
 * 
 * @author Lianxw
 * 
 */
public class ECGChartFragment extends Fragment implements OnClickListener {

	private static final String KEY_RANGE = "range";
	private StaticSplineChartView charHR;
	private StaticSplineChartView charHRV;
	private StaticSplineChartView charMOOD;

	public static ECGChartFragment newInstance(int range) {
		if (range < 1 || range > 4) {
			throw new IllegalArgumentException("Illegal Range");
		}
		ECGChartFragment fragment = new ECGChartFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_RANGE, range);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ecg_detail, container, false);
		charHR = (StaticSplineChartView) view.findViewById(R.id.chart_hr);
		charHRV = (StaticSplineChartView) view.findViewById(R.id.chart_hrv);
		charMOOD = (StaticSplineChartView) view.findViewById(R.id.chart_mood);
		charHR.setOnClickListener(this);
		charHRV.setOnClickListener(this);
		charMOOD.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refresh();
	}

	public void refresh() {
		int rangeType = getArguments().getInt(KEY_RANGE);
		Range range = RangeUtil.getRange(rangeType);
		MHealthProviderMetaData provider = MHealthProviderMetaData.GetMHealthProvider(getActivity());
		List<DataECG> ecgDataList = provider.getEcgDataByTime(range.getStartTime(), range.getEndTime());
		if (ecgDataList != null) {
			System.out.println("--------ecgDataList----------"+ecgDataList.size());
			List<DataECG> ecgDataFilteredList = new ECGDataFilter().filter(rangeType, ecgDataList);
			if (ecgDataFilteredList != null && ecgDataFilteredList.size() > 0) {
				System.out.println("---------ecgDataFilteredList------------" + ecgDataFilteredList.size());
				bindLatestAndAverage(ecgDataFilteredList);
				bindData(ecgDataFilteredList);
			}
		} else {
			Toast.makeText(getActivity(), "当前视图无数据", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 绑定图表
	 * 
	 * @param ecgDataList
	 */
	private void bindData(List<DataECG> ecgDataList) {
		List<PointD> mapHr = new ArrayList<PointD>();
		List<PointD> mapHrv = new ArrayList<PointD>();
		List<PointD> mapMood = new ArrayList<PointD>();
		double x = 0;
		double maxHr = 0;
		double maxHrv = 0;
		double maxMood = 0;
		for (int i = 0; i < ecgDataList.size(); i++) {
			DataECG ecg = ecgDataList.get(i);
			double hr = Double.valueOf(ecg.data.hr);
			mapHr.add(new PointD(x, hr));
			maxHr = hr > maxHr ? hr : maxHr;

			double hrv = Double.valueOf(ecg.data.hrv);
			mapHrv.add(new PointD(x, hrv));
			maxHrv = hrv > maxHrv ? hrv : maxHrv;

			double mood = Double.valueOf(ecg.data.mood);
			mapMood.add(new PointD(x, mood));
			maxMood = mood > maxMood ? mood : maxMood;
			x += 10;
		}
		List<String> labels = new ArrayList<String>();
		for (int i = 0; i < x; i += 10) {
			labels.add(String.valueOf(i));
		}
		// HR
		int max = MathUtil.ceil(maxHr);
		charHR.setDataAxisMax(max + 10);
		charHR.setLabelAxisMax(x);
		charHR.setDataset(mapHr);
		charHR.setLabel(labels);
		// HRV
		max = MathUtil.ceil(maxHrv);
		charHRV.setDataAxisMax(max + 10);
		charHRV.setLabelAxisMax(x);
		charHRV.setDataset(mapHrv);
		charHRV.setLabel(labels);
		// MOOD
		max = MathUtil.ceil(maxMood);
		charMOOD.setDataAxisMax(max + 10);
		charMOOD.setLabelAxisMax(x);
		charMOOD.setDataset(mapMood);
		charMOOD.setLabel(labels);
	}

	private void bindLatestAndAverage(List<DataECG> ecgDataList) {
		if (ecgDataList.size() == 0) {
			return;
		}
		double hrTotal = 0;
		double hrvTotal = 0;
		double moodTotal = 0;
		for (int i = 0; i < ecgDataList.size(); i++) {
			DataECG ecg = ecgDataList.get(i);
			double hr = Double.valueOf(ecg.data.hr);
			hrTotal += hr;

			double hrv = Double.valueOf(ecg.data.hrv);
			hrvTotal += hrv;

			double mood = Double.valueOf(ecg.data.mood);
			moodTotal += mood;
		}
		View root = getView();
		int size = ecgDataList.size();
		((TextView) root.findViewById(R.id.textView_hr_av)).setText(String.format("%.1f", hrTotal / size));
		((TextView) root.findViewById(R.id.textView_hrv_av)).setText(String.format("%.1f", hrvTotal / size));
		((TextView) root.findViewById(R.id.textView_stress_av)).setText(String.format("%.1f", moodTotal / size));

		((TextView) root.findViewById(R.id.textView_hr)).setText(ecgDataList.get(size - 1).data.hr);
		((TextView) root.findViewById(R.id.textView_hrv)).setText(ecgDataList.get(size - 1).data.hrv);
		((TextView) root.findViewById(R.id.textView_stress)).setText(ecgDataList.get(size - 1).data.mood);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.chart_hr || id == R.id.chart_hrv || id == R.id.chart_mood) {
			StaticSplineChartView chart = (StaticSplineChartView) v;
			if (chart.hasData()) {
				Intent intent = new Intent(getActivity(), ECGDetailChartActivity.class);
				int type = (id == R.id.chart_hr ? ECGDetailChartActivity.TYPE_HR
						: (id == R.id.chart_hrv ? ECGDetailChartActivity.TYPE_HRV : ECGDetailChartActivity.TYPE_MOOD));
				intent.putExtra(ECGDetailChartActivity.EXTRA_TYPE, type);
				intent.putExtra(ECGDetailChartActivity.EXTRA_RANGE, getArguments().getInt(KEY_RANGE));
				startActivity(intent);
			}
		}
	}
}
