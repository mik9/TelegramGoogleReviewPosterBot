package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changechat

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.exceptions.TelegramApiException
import ua.pl.mik.kakashkaposterbot.Scheduler
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseCallbackQueryHandler
import ua.pl.mik.kakashkaposterbot.db.Database
import ua.pl.mik.kakashkaposterbot.db.models.App
import ua.pl.mik.kakashkaposterbot.db.models.Chat
import ua.pl.mik.kakashkaposterbot.db.models.ChatState
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils

import java.util.Optional

import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId

class ChangeChatAppNameHandler : BaseCallbackQueryHandler() {

    override fun handleCallbackQuery(update: Update): Boolean {
        val chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update))
        if (chat.state != ChatState.WAITING_FOR_APP_TO_CHANGE_CHAT) {
            return false
        }
        val userText = update.callbackQuery.data

        //        if (!TelegramUtils.isManagementAllowed(update.getCallbackQuery().getMessage().getChat(),
        //                update.getCallbackQuery().getMessage().getFrom())) {
        //
        //        }

        val appSet: Set<App>
        if (update.callbackQuery.message.chat.isUserChat!!) {
            appSet = Database.get().listAppsByUserId(getUserId(update))
        } else {
            appSet = Database.get().listAppsByChatId(getChatId(update), getUserId(update))
        }

        val appOptional = appSet
                .stream()
                .filter { app -> app.id == java.lang.Long.parseLong(userText.split(" ")[1]) }
                .findAny()

        if (appOptional.isPresent) {
            val app = appOptional.get()
            val pendingApp = PendingApp(app)

            Database.get().savePendingApp(pendingApp)
            Database.get().deleteApp(app)
            Scheduler.unSchedule(app)

            val sendMessage = SendMessage()
            sendMessage.replyMarkup = ReplyKeyboardRemove()
            sendMessage.setChatId(getChatId(update))
            sendMessage.text = READY_MESSAGE.format(pendingApp.pendingAppId)

            TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage)
        } else {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Не знаю такої програми: \"$userText\"")
        }

        return true
    }
    companion object {
        const val READY_MESSAGE = "Ви можете додати мене у групу за наступним посиланням:\nhttps://telegram.me/kakashkaposterbot?startgroup=%d"
    }
}
