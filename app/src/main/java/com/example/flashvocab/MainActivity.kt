package com.example.flashvocab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.flashvocab.ui.FlashVocabViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import java.util.concurrent.ThreadLocalRandom.current
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashvocab.data.db.AppDatabase

import com.example.flashvocab.ui.theme.FlashVocabTheme
import com.example.flashvocab.data.db.entity.VocabEntity

// =======================
// Data models
// =======================

// 단어장 (List-level entity)


class MainActivity : ComponentActivity() {

    private val viewModel: FlashVocabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FlashVocabTheme {

                // =======================
                // ViewModel → UI state
                // =======================
                val wordLists by viewModel.wordLists.collectAsState()
                val vocabs by viewModel.vocabs.collectAsState()
                var selectedListId by remember { mutableStateOf<Int?>(null) }
                var index by remember { mutableStateOf(0) }
                var showWrongOnly by remember { mutableStateOf(false) }
                var showCreateListDialog by remember { mutableStateOf(false) }
                var newListName by remember { mutableStateOf("") }
                // 단어 추가 Dialog state
                var showAddWordDialog by remember { mutableStateOf(false) }
                var newFront by remember { mutableStateOf("") }
                var newBack by remember { mutableStateOf("") }
                var showDeleteVocabDialog by remember { mutableStateOf(false) }

                // 단어장 삭제 Dialog state
                var showDeleteListDialog by remember { mutableStateOf(false) }

                var currentVocab by remember { mutableStateOf<VocabEntity?>(null) }
                BackHandler(enabled = selectedListId != null) {
                    selectedListId = null
                    index = 0
                    showWrongOnly = false
                }


                Scaffold { innerPadding ->

                    // =======================
                    // 단어장 선택 화면
                    // =======================
                    if (selectedListId == null) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "단어장 선택",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            wordLists.forEach { list ->
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        selectedListId = list.id
                                        viewModel.selectList(list.id)
                                        index = 0
                                        showWrongOnly = false
                                    }
                                ) {
                                    Text(list.name)
                                }
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    newListName = ""
                                    showCreateListDialog = true
                                }
                            ) {
                                Text("+ 새 단어장 만들기")
                            }
                        }

                    } else {

                        // =======================
                        // 카드 학습 화면
                        // =======================

                        val filteredList = if (showWrongOnly) {
                            vocabs.filter { it.dontKnowCount > 0 }
                        } else {
                            vocabs
                        }


                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // =======================
                            // 상단 헤더 +단어 / 삭제
                            // =======================
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        selectedListId = null
                                        index = 0
                                        showWrongOnly = false
                                    }
                                ) {
                                    Text("← 단어장 선택")
                                }

                                Row {
                                    Button(
                                        onClick = {
                                            newFront = ""
                                            newBack = ""
                                            showAddWordDialog = true
                                        }
                                    ) {
                                        Text("+ 단어")
                                    }
                                }


                                Spacer(modifier = Modifier.width(8.dp))


                                Button(
                                    onClick = {
                                        showDeleteVocabDialog = true
                                    }
                                ) {
                                    Text("단어 삭제")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        showDeleteListDialog = true
                                    }
                                ) {
                                    Text("단어장 삭제")
                                }
                            }
                            if (filteredList.isEmpty()) {

                                Text("단어가 없습니다")

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        newFront = ""
                                        newBack = ""
                                        showAddWordDialog = true
                                    }
                                ) {
                                    Text("+ 첫 단어 추가")
                                }

                            } else {

                                val current = filteredList[index % filteredList.size]
                                currentVocab = current

                                FlashCard(
                                    front = current.front,
                                    back = current.back
                                )




                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            index =
                                                (index - 1 + filteredList.size) % filteredList.size
                                        }
                                    ) {
                                        Text("Prev")
                                    }

                                    Button(
                                        onClick = {
                                            index = (0 until filteredList.size).random()
                                        }
                                    ) {
                                        Text("Shuffle")
                                    }

                                    Button(
                                        onClick = {
                                            index = (index + 1) % filteredList.size
                                        }
                                    ) {
                                        Text("Next")
                                    }
                                }

                                // =======================
                                // 학습 액션
                                // =======================
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.know(current)
                                            index = (index + 1) % filteredList.size
                                        }
                                    ) {
                                        Text("Know")
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.dontKnow(current)
                                            index = (index + 1) % filteredList.size
                                        }
                                    ) {
                                        Text("Don't know")
                                    }
                                }

                                // =======================
                                // Wrong only toggle
                                // =======================
                                Button(
                                    onClick = {
                                        showWrongOnly = !showWrongOnly
                                        index = 0
                                    }
                                ) {
                                    Text(
                                        if (showWrongOnly) "Show All"
                                        else "Wrong Only"
                                    )
                                }
                            }
                        }
                    }
                }

                if (showCreateListDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showCreateListDialog = false
                        },
                        title = {
                            Text("새 단어장 만들기")
                        },
                        text = {
                            OutlinedTextField(
                                value = newListName,
                                onValueChange = { newListName = it },
                                label = { Text("단어장 이름") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            Button(
                                enabled = newListName.isNotBlank(),
                                onClick = {
                                    viewModel.createWordList(newListName) { id ->
                                        selectedListId = id
                                        viewModel.selectList(id)
                                        index = 0
                                        showWrongOnly = false
                                    }
                                    showCreateListDialog = false
                                }
                            ) {
                                Text("생성")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showCreateListDialog = false }
                            ) {
                                Text("취소")
                            }
                        }
                    )
                }

                // 단어 추가
                if (showAddWordDialog && selectedListId != null) {
                    AlertDialog(
                        onDismissRequest = { showAddWordDialog = false },
                        title = { Text("단어 추가") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newFront,
                                    onValueChange = { newFront = it },
                                    label = { Text("Word") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newBack,
                                    onValueChange = { newBack = it },
                                    label = { Text("Meaning") },
                                    singleLine = true
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                enabled = newFront.isNotBlank() && newBack.isNotBlank(),
                                onClick = {
                                    viewModel.addVocab(
                                        listId = selectedListId!!,
                                        front = newFront,
                                        back = newBack
                                    )
                                    index = 0            // ⭐ 반드시 필요
                                    showAddWordDialog = false
                                }
                            ) {
                                Text("추가")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddWordDialog = false }) {
                                Text("취소")
                            }
                        }
                    )
                }

                // 단어 삭제
                if (showDeleteVocabDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteVocabDialog = false },
                        title = { Text("단어 삭제") },
                        text = { Text("이 단어를 삭제할까요?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    currentVocab?.let {
                                        viewModel.deleteVocab(it)
                                    }
                                    index = 0
                                    showDeleteVocabDialog = false
                                }
                            ) {
                                Text("삭제")
                            }

                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDeleteVocabDialog = false }
                            ) {
                                Text("취소")
                            }
                        }
                    )
                }

                // 단어장 삭제
                if (showDeleteListDialog && selectedListId != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteListDialog = false },
                        title = { Text("단어장 삭제") },
                        text = { Text("이 단어장을 정말 삭제할까요?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteWordList(selectedListId!!)
                                    selectedListId = null
                                    index = 0
                                    showWrongOnly = false
                                    showDeleteListDialog = false
                                }
                            ) {
                                Text("삭제")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteListDialog = false }) {
                                Text("취소")
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun FlashCard(
    front: String,
    back: String
) {
    var isFront by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { isFront = !isFront },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isFront) {
                Text(
                    text = front,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = back,
                    fontSize = 24.sp
                )
            }
        }
    }
}
