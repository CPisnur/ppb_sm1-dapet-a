package com.tugas.kuissatuppb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Model Data Mahasiswa
data class StudentModel(
    val id: String,
    val nim: String,
    val name: String,
    val studyProgram: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentManagerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagerApp() {
    var currentScreen by remember { mutableStateOf("home") }

    var studentList by remember {
        mutableStateOf(
            listOf(
                StudentModel("1", "1302210001", "Budi Santoso", "Teknik Informatika"),
                StudentModel("2", "1302210002", "Siti Aminah", "Sistem Informasi"),
                StudentModel("3", "1302210003", "Andi Wijaya", "Teknik Komputer")
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<StudentModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val filteredStudents = studentList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.nim.contains(searchQuery, ignoreCase = true)
    }

    when (currentScreen) {
        "home" -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Student Manager") },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { currentScreen = "add" }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Mahasiswa")
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari mahasiswa...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (filteredStudents.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada data mahasiswa",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredStudents) { student ->
                                StudentCard(
                                    student = student,
                                    onEdit = {
                                        selectedStudent = student
                                        currentScreen = "edit"
                                    },
                                    onDelete = {
                                        selectedStudent = student
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog && selectedStudent != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Hapus Mahasiswa?") },
                    text = { Text("Apakah Anda yakin ingin menghapus data ${selectedStudent?.name}?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                studentList = studentList.filter { it.id != selectedStudent?.id }
                                showDeleteDialog = false
                                selectedStudent = null
                            }
                        ) {
                            Text("Hapus", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }

        "add" -> {
            StudentFormScreen(
                title = "Tambah Mahasiswa",
                initialNim = "",
                initialName = "",
                initialProgram = "Teknik Informatika",
                onSave = { nim, name, program ->
                    val newStudent = StudentModel(
                        id = System.currentTimeMillis().toString(),
                        nim = nim,
                        name = name,
                        studyProgram = program
                    )
                    studentList = studentList + newStudent
                    currentScreen = "home"
                },
                onCancel = { currentScreen = "home" }
            )
        }

        "edit" -> {
            selectedStudent?.let { student ->
                StudentFormScreen(
                    title = "Edit Mahasiswa",
                    initialNim = student.nim,
                    initialName = student.name,
                    initialProgram = student.studyProgram,
                    onSave = { nim, name, program ->
                        studentList = studentList.map {
                            if (it.id == student.id) {
                                it.copy(nim = nim, name = name, studyProgram = program)
                            } else {
                                it
                            }
                        }
                        currentScreen = "home"
                        selectedStudent = null
                    },
                    onCancel = {
                        currentScreen = "home"
                        selectedStudent = null
                    }
                )
            }
        }
    }
}

@Composable
fun StudentCard(
    student: StudentModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "NIM: ${student.nim}", style = MaterialTheme.typography.bodyMedium)
                Text(text = student.studyProgram, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    title: String,
    initialNim: String,
    initialName: String,
    initialProgram: String,
    onSave: (String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var nim by remember { mutableStateOf(initialNim) }
    var name by remember { mutableStateOf(initialName) }
    var selectedProgram by remember { mutableStateOf(initialProgram) }

    var expanded by remember { mutableStateOf(false) }
    val programs = listOf("Teknik Informatika", "Sistem Informasi", "Teknik Komputer", "Manajemen", "Akuntansi")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        // Menggunakan ikon AutoMirrored agar tidak deprecated
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Field NIM dikunci hanya angka dan menggunakan keyboard angka
            OutlinedTextField(
                value = nim,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        nim = input
                    }
                },
                label = { Text("NIM") },
                placeholder = { Text("Masukkan angka saja") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProgram,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Program Studi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    programs.forEach { program ->
                        DropdownMenuItem(
                            text = { Text(program) },
                            onClick = {
                                selectedProgram = program
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = { if (nim.isNotBlank() && name.isNotBlank()) onSave(nim, name, selectedProgram) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}