#include <pebble.h>
#include <stdlib.h>
#include <stdio.h>
  
static Window *window;
static TextLayer *text_layer;

int i = 0;
int number = 0;

void up_long_click_handler(ClickRecognizerRef recognizer, void *context) {
  
  i = (i < 50) ? i + 1 : i;
  static char buf[] = "123456";
  snprintf(buf, sizeof(buf), "%d", i);
  text_layer_set_text(text_layer, buf);
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  text_layer_set_text(text_layer, "Select");
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
  
  i = (i < 50) ? i + 1 : i;
  static char buf[] = "123456";
  snprintf(buf, sizeof(buf), "%d", i);
  text_layer_set_text(text_layer, buf);
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  
  i = (i > 0) ? i - 1 : i;
  static char buf[] = "123456";
  snprintf(buf, sizeof(buf), "%d", i);
  text_layer_set_text(text_layer, buf);
  
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  text_layer = text_layer_create((GRect) { .origin = { 0, 72 }, .size = { bounds.size.w, 20 } });
  text_layer_set_text(text_layer, "Press a button");
  text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(text_layer));
}

static void window_unload(Window *window) {
  text_layer_destroy(text_layer);
}


static void inbox_received_callback(DictionaryIterator *iterator, void *context) {

    Tuple *t = dict_read_first(iterator);
    number = (int)t->value->int32; 
  
    
    static char buf[] = "123456";
    snprintf(buf, sizeof(buf), "%d", i);
    //text_layer_set_text(&countLayer, buf);
  
    //char buffer[20];
    //itoa(number, buffer, 10);
    text_layer_set_text(text_layer, "number");
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped!");
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed!");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
  APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}


static void init(void) {
  window = window_create();
  window_set_click_config_provider(window, click_config_provider);
  window_set_window_handlers(window, (WindowHandlers) {
	.load = window_load,
    .unload = window_unload,
  });
  
  // Register callbacks
  app_message_register_inbox_received(inbox_received_callback);
  app_message_register_inbox_dropped(inbox_dropped_callback);
  app_message_register_outbox_failed(outbox_failed_callback);
  app_message_register_outbox_sent(outbox_sent_callback);
  
  // Register callbacks
  app_message_register_inbox_received(inbox_received_callback);
  app_message_register_inbox_dropped(inbox_dropped_callback);
  app_message_register_outbox_failed(outbox_failed_callback);
  app_message_register_outbox_sent(outbox_sent_callback);
  
  const bool animated = true;
  window_stack_push(window, animated);
}

static void deinit(void) {
  window_destroy(window);
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}