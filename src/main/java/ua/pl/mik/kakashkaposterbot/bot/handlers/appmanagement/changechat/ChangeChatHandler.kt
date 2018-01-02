package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changechat

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl
import ua.pl.mik.kakashkaposterbot.bot.WrongStateException
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler
import ua.pl.mik.kakashkaposterbot.db.Database
import ua.pl.mik.kakashkaposterbot.db.models.ChatState
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId

class ChangeChatHandler : BaseTextMessageHandler() {
    override fun handleTextMessage(update: Update): Boolean {
        if (!update.message.text.startsWith("/changechat")) {
            return false
        }

        val chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update))
        if (chat.state != ChatState.NO_STATE) {
            throw WrongStateException(chat.state)

        }
        val keyboard = TelegramUtils.createBotInlineSelectionKeyboard(update, "changechat")

        if (keyboard.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає завдань.")
            return true
        }

        chat.state = ChatState.WAITING_FOR_APP_TO_CHANGE_CHAT
        Database.get().saveChat(chat)

        val sendMessage = SendMessage()
        sendMessage.replyMarkup = InlineKeyboardMarkup()
                .setKeyboard(keyboard)
        sendMessage.setChatId(getChatId(update))
        sendMessage.text = "Яке з завдань ви хочете перемістити в інший чат?"

        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage)

        return true
    }
}