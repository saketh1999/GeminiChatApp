package com.example.geminichatapp

import android.graphics.Bitmap
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminichatapp.data.Chat
import com.example.geminichatapp.data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel(){
    private  val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    fun onEvent(event:ChatUIEvent)
    {
        when(event)
        {
            is ChatUIEvent.SendPrompt -> {
                if(chatState.value.prompt.isNotEmpty())
                {
                    addPrompt(event.prompt,event.bitmap)
                }
                if(event.prompt.isNotEmpty())
                {
                    addPrompt(event.prompt,event.bitmap)

                    if (event.bitmap!=null)
                    {
                        getResponseImage(event.prompt, event.bitmap)
                    }
                    else
                    {
                        getResponse(event.prompt)
                    }
                }

            }

            is ChatUIEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }


        }


    }

    private fun addPrompt (prompt:String,bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableStateList().apply {
                    add(0, Chat(prompt,bitmap,true))
                },
                prompt = "",
                bitmap = null
            )
        }

    }

    private fun getResponse(prompt: String)
    {
        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt)
            _chatState.update {
                it.copy(
                    chatList =  it.chatList.toMutableStateList().apply {
                        add(0,chat)
                    }
                )
            }
        }
    }
    private fun getResponseImage(prompt: String, bitmap: Bitmap)
    {
        viewModelScope.launch {
            val chat = ChatData.getResponseWithImage(prompt,bitmap)
            _chatState.update {
                it.copy(
                    chatList =  it.chatList.toMutableStateList().apply {
                        add(0,chat)
                    }
                )
            }
        }
    }
}