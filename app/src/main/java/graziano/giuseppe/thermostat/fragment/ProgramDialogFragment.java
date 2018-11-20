package graziano.giuseppe.thermostat.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Program;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.network.HttpClient;

public class ProgramDialogFragment extends DialogFragment {

    private static final String ARG_PROGRAM = "program";

    private Program program;

    private NumberPicker numberPikerWeekDay;
    private ToggleButton toggleButton;
    private TimePicker timePicker;
    boolean statusChange = false;

   // private OnFragmentInteractionListener mListener;

    public ProgramDialogFragment() {
        // Required empty public constructor
    }

    public static ProgramDialogFragment newInstance(Program program) {
        ProgramDialogFragment fragment = new ProgramDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROGRAM, program);
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable(ARG_PROGRAM);
            if(program.getId() == 0L){
                program.setWeekDay(LocalDate.now().getDayOfWeek());
                program.setStartTime(LocalTime.now());
                program.setEndTime(LocalTime.now());
            }
            if(program.getStartTime() == null){
                program.setStartTime(LocalTime.now());
            }
            if(program.getEndTime() == null){
                program.setEndTime(LocalTime.now());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.fragment_program_dialog, null);

        numberPikerWeekDay = (NumberPicker) view.findViewById(R.id.weekDayPiker);
        timePicker = (TimePicker) view.findViewById(R.id.timePiker);
        toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);

        final String days[] = getResources().getStringArray(R.array.week_days);


        numberPikerWeekDay.setMinValue(0);
        numberPikerWeekDay.setMaxValue(DayOfWeek.values().length - 1);
        numberPikerWeekDay.setDisplayedValues(days);
        numberPikerWeekDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //  Toast..setText("Value: " + days[newVal]);
              //  Toast.makeText(getContext(), DayOfWeek.values()[newVal].toString(), Toast.LENGTH_SHORT).show();

            }
        };
        numberPikerWeekDay.setValue(Arrays.asList(days).indexOf(program.getWeekDay()));


        numberPikerWeekDay.setOnValueChangedListener(myValChangedListener);

        if(toggleButton.isChecked()){
            timePicker.setHour(program.getEndTime().getHour());
            timePicker.setMinute(program.getEndTime().getMinute());
        }
        else {
            timePicker.setHour(program.getStartTime().getHour());
            timePicker.setMinute(program.getStartTime().getMinute());
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statusChange = true;
                if(isChecked){

                    timePicker.setHour(program.getEndTime().getHour());
                    timePicker.setMinute(program.getEndTime().getMinute());

                }
                else {
                    timePicker.setHour(program.getStartTime().getHour());
                    timePicker.setMinute(program.getStartTime().getMinute());

                }
            }
        });


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(statusChange){
                    statusChange = false;
                    return;
                }
                if(toggleButton.isChecked()){
                  program.setEndTime(LocalTime.of(hourOfDay, minute));
                }
                else {
                    program.setStartTime(LocalTime.of(hourOfDay, minute));
                }
            }
        });

        timePicker.setIs24HourView(true);

        builder.setView(view)
                .setTitle("Configura programma")
                .setPositiveButton("Salva", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       DayOfWeek selectedDay =  DayOfWeek.values()[numberPikerWeekDay.getValue()];
                       program.setWeekDay(selectedDay);
                        Thermostat thermostat = MainActivity.user.getSelectedThermostat();
                        Set<Program> programs = thermostat.getProgramMode().getPrograms();
                        if(!programs.contains(program)){
                            programs.add(program);
                           // program.setProgramMode(thermostat.getProgramMode());
                        }
                        HttpClient.putThermostat(thermostat, null, null);
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("Elimina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thermostat thermostat = MainActivity.user.getSelectedThermostat();
                        Set<Program> programs = thermostat.getProgramMode().getPrograms();
                        if(programs.contains(program)){
                            programs.remove(program);
                            HttpClient.putThermostat(thermostat, null, null);
                        }

                    }
                });


        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorActive));
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorBackground));
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });



        return dialog;


    }


    void saveProgram(){

    }


    void deleteProgarm(){

    }
}
