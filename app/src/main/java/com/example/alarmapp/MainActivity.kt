package com.example.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Creamos el Intent y para el 2° parámetro, podemos pasarle un servicio, clase de un
        //broadcast o clase de una activity
        //En este caso, indicaremos que se ejecute esta activity
        val unIntent = Intent(this, MainActivity::class.java)

        //Creamos un PendingIntent que incluya al Intent y dependiendo que parámetro le pasamos al
        //Intent,usamos el getService(), getBroadcast() o getActivity()
        val unPendingIntent = PendingIntent.getActivity(
            this,
            1,
            unIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Obtenemos el servicio standard de alarmas y como sabemos que ALARM_SERVICE es un
        //AlarmManager, lo casteamos
        val unAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Seteamos lógica para que se ejecute una alarma luego de 5 segundos
        btn_tiempo_transcurrido.setOnClickListener {
            //Declaramos variable tipo Tiempo transcurrido (Elapsed Real-Time, ERT)
            val unTipoAlarma = AlarmManager.ELAPSED_REALTIME

            //Tiempo desde que se inició el celu + 3 segundos, que equivale a hora actual + 3 seg,
            //así se ejecutará dentro de 3 segundos desde que se haga click
            val horaActualMas3segundos: Long = SystemClock.elapsedRealtime() + 3000

            //Indicamos que tipo de alarma será, en cuánto se ejecutará y qué se ejecutará
            unAlarmManager.set(
                unTipoAlarma,
                horaActualMas3segundos,
                unPendingIntent
            )

            //Avisamos al usuario que la alarma se ejecutará
            Toast.makeText(this, "Alarma ejecutará en 3 segundos", Toast.LENGTH_SHORT)
                .show()
        }

        //Seteamos lógica para que se ejecute una alarma diariamente alrededor del horario
        //elegido por el usuario
        btn_rtc.setOnClickListener {
            //Continuar solo si el usuario eligió un horario
            if (tv_hora_elegida.text == "")
                Toast.makeText(this, "Elegir horario para ejecutar alarma RTC", Toast.LENGTH_SHORT)
                    .show()
            else {
                //Declaramos variable tipo Horario reloj (Real-Time Clock, RTC)
                val unTipoAlarma = AlarmManager.RTC

                //Para sacar el día de hoy, creamos un Calendar
                val unCalendario = Calendar.getInstance()
                //Le pasamos el tiempo transcurrido desde que se inició el celu
                unCalendario.timeInMillis = SystemClock.elapsedRealtime()
                //Recuperamos y asignamos el horario elegido por el usuario
                val hour: Int = Integer.parseInt(tv_hora_elegida.text.substring(0, 2));
                val minute: Int = Integer.parseInt(tv_hora_elegida.text.substring(3));
                unCalendario.set(Calendar.HOUR, hour)
                unCalendario.set(Calendar.MINUTE, minute)

                //Indicamos que queremos que se repita la alarma diariamente, que no es necesario
                //que exactamente ejecute en el horario elegido, sino que lo haga en un horario
                //alrededor del mismo cuando el sistema tenga disponibilidad de recursos para
                //hacerlo (setInexactRepeating)
                unAlarmManager.setInexactRepeating(
                    unTipoAlarma,
                    unCalendario.timeInMillis, //Paso la hora de ejecución de la alarma
                    AlarmManager.INTERVAL_DAY, //Para que se ejecute cada 1 día
                    unPendingIntent
                )
            }
        }

        //Lógica para abrir reloj para que el usuario elija horario al clickear el edit text
        btn_elegir_hora.setOnClickListener {
            //Creamos un TimePickerDialog el cual contendrá un horario y este quedará mapeado a
            //nuestro TextView en formato HH:MM
            val unCalendar = Calendar.getInstance()
            val unTimePickerDialog =
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    unCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    unCalendar.set(Calendar.MINUTE, minute)
                    tv_hora_elegida.text = SimpleDateFormat("HH:mm").format(unCalendar.time)
                }

            //Mostramos el reloj para elegir horario y eso se almacenará en nuestro TiemPickerDialog
            TimePickerDialog(
                this,
                unTimePickerDialog,
                unCalendar.get(Calendar.HOUR_OF_DAY),
                unCalendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}