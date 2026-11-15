# Zapisy na wycieczkę

Formularz: EditText, Spinner z string-array, DatePickerDialog, SeekBar,
Switch, CheckBox. Walidacja imienia, kierunku i daty, potwierdzenie
w AlertDialog, Snackbar z cofnięciem. Przycisk "Wyczyść formularz".

<img width="1080" height="2400" alt="Screenshot_20261116_002801" src="https://github.com/user-attachments/assets/ea16a956-acdf-4687-9c39-8ed0f9445653" />


## Jak rozwiązałem cofanie czyszczenia

Przed wyczyszczeniem zapisuję wartości wszystkich kontrolek w obiekcie
klasy FormState (captureState). Obiekt trafia do zmiennej lokalnej, którą
widzi lambda przycisku "Cofnij" w Snackbarze. Kliknięcie wywołuje
restoreState, które wpisuje zapamiętane wartości z powrotem do kontrolek.
