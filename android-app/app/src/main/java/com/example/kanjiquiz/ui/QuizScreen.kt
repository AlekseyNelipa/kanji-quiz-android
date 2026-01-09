package com.example.kanjiquiz.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreen(viewModel: QuizViewModel, modifier: Modifier) {
    val stateData by viewModel.stateData.collectAsState()
    when (stateData.quizPhase) {
        QuizPhase.Question -> Question(
            viewModel = viewModel,
            modifier = modifier
        )

        QuizPhase.CorrectAnswer -> Answer(
            viewModel = viewModel,
            isAnswerCorrect = true,
            modifier = modifier
        )

        QuizPhase.IncorrectAnswer -> Answer(
            viewModel = viewModel,
            isAnswerCorrect = false,
            modifier = modifier
        )

        QuizPhase.Loading -> Loading(modifier = modifier)
        QuizPhase.Empty -> Empty(modifier = modifier)
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
private fun Question(viewModel: QuizViewModel, modifier: Modifier = Modifier) {
    val stateData by viewModel.stateData.collectAsState()
    val currentEntry = stateData.currentEntry ?: return

    val focusRequester = remember {FocusRequester()}
    ScreenCard(modifier) {
        Text(currentEntry.expression, fontSize = 50.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = stateData.answer,
            singleLine = true,
            onValueChange = viewModel::setAnswer,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { viewModel.submit() }),
            label = { Text("Enter text", fontSize = 20.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = 25.sp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        if(stateData.validationMessage!="") {
            Text(stateData.validationMessage,
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
    viewModel: QuizViewModel,
    isAnswerCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val stateData by viewModel.stateData.collectAsState()
    val currentEntry = stateData.currentEntry ?: return
    ScreenCard(modifier) {

        Text(currentEntry.expression, fontSize = 50.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            if (isAnswerCorrect) "Correct" else "Wrong",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAnswerCorrect) Color.Green else Color.Red
        )

        Text(
            "Your answer: ${stateData.answer}",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Correct: ${currentEntry.reading}",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Meaning: ${currentEntry.meaning}",
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
