package com.example.kanjiquiz.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanjiquiz.app.App
import com.example.kanjiquiz.data.VocabEntry
import com.example.kanjiquiz.ui.theme.KanjiQuizTheme


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val app = application as App
        MainViewModelFactory(app.vocabRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanjiQuizTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (viewModel.quizState.value) {
            QuizState.Question -> Question(
                viewModel = viewModel,
                modifier = modifier
            )

            QuizState.CorrectAnswer -> Answer(
                viewModel = viewModel,
                isAnswerCorrect = true,
                modifier = modifier
            )

            QuizState.IncorrectAnswer -> Answer(
                viewModel = viewModel,
                isAnswerCorrect = false,
                modifier = modifier
            )

            QuizState.Loading -> Loading(modifier = modifier)
            QuizState.Empty -> Empty(modifier = modifier)
        }
    }
}

@Composable
private fun Loading(modifier: Modifier) {
    ScreenCard(modifier) {
        Text("Loading", fontSize = 50.sp, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Empty(modifier: Modifier) {
    ScreenCard(modifier) {
        Text("Empty", fontSize = 50.sp, modifier = Modifier.fillMaxWidth())
    }
}


@Composable
private fun Question(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val currentItem = viewModel.currentItem.value
    if (currentItem == null)
        return
    val focusRequester = remember {FocusRequester()}
    ScreenCard(modifier) {
        Text(currentItem.expression, fontSize = 50.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = viewModel.answer.value,
            singleLine = true,
            onValueChange = { viewModel.answer.value = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { viewModel.submit() }),
            label = { Text("Enter text", fontSize = 20.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = 25.sp),
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
        )

        if(viewModel.validationMessage.value!="") {
            Text(viewModel.validationMessage.value,
                fontSize = 20.sp, color = Color.Red, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { viewModel.submit() }, Modifier
            .fillMaxWidth()
            .size(64.dp)) {
            Text("Submit", fontSize = 25.sp)
        }

        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

    }
}

@Composable
private fun Answer(
    viewModel: MainViewModel,
    isAnswerCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val currentItem = viewModel.currentItem.value ?: return
    ScreenCard(modifier) {

        Text(currentItem.expression, fontSize = 50.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            if (isAnswerCorrect) "Correct" else "Wrong",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAnswerCorrect) Color.Green else Color.Red
        )

        Text(
            "Your answer: ${viewModel.answer.value}",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Correct: ${currentItem.reading}",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Meaning: ${currentItem.meaning}",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { viewModel.next() }, Modifier
            .fillMaxWidth()
            .size(64.dp)) {
            Text("Next", fontSize = 25.sp)
        }
    }

}

@Composable
private fun ScreenCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Card(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    KanjiQuizTheme {
        val viewModel = MainViewModel(object : VocabRepository {
            override suspend fun getAll(): List<VocabEntry> = listOf(
                VocabEntry(
                    id = 1,
                    expression = "犬",
                    reading = "いぬ",
                    meaning = "dog",
                    vocabSet = "jlpt-n5"
                )
            )
        })
        MainScreen(viewModel)
    }
}