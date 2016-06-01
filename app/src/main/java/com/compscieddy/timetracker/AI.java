package com.compscieddy.timetracker;

/**
 * Created by elee on 5/25/16.
 */
public class AI {

  public static int getIconId(String title) {

    /** There should be a priority to this "AI".
     *  Maybe verbs/actions, objects, then location (home, office)
     */

    int iconId = -1;
    if (Utils.containsAtLeastOne(title, new String[]{
        "running", "run", "ran", "jog", "marathon"})) {
      iconId = R.drawable.ic_directions_run_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "drive", "car", "jeep", "convertible", "truck", "sedan", "coupe", "hatchback"})) {
      iconId = R.drawable.ic_directions_car_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "gym", "weight", "curls", "bench", "yoked", "buff", "workout", "working out", "work out"})) {
      iconId = R.drawable.ic_fitness_center_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "beach", "sand", "waves"})) {
      iconId = R.drawable.ic_beach_access_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "cafe", "coffee", "starbucks", "peets", "peet's", "philz"})) {
      iconId = R.drawable.ic_local_cafe_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "shopping", "mall", "grocer", "shop", "buy", "amazon", "costco", "safeway"})) {
      iconId = R.drawable.ic_shopping_cart_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "call", "phone", "buzz", "ring"})) {
      iconId = R.drawable.ic_call_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "note", "idea", "organize", "track"})) {
      iconId = R.drawable.ic_note_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "music", "sing", "audio", "song", "band", "concert", "show", "jazz"})) {
      iconId = R.drawable.ic_music_note_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "read", "book", "page", "article"})) {
      iconId = R.drawable.ic_book_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "movie", "theater"})) {
      iconId = R.drawable.ic_theaters_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "tv", "game of thrones"})) {
      iconId = R.drawable.ic_live_tv_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "park", "picnic", "nature"})) {
      iconId = R.drawable.ic_nature_people_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "draw", "sketch", "doodle", "art", "museum", "design"})) {
      iconId = R.drawable.ic_gesture_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "paint", "watercolor"})) {
      iconId = R.drawable.ic_palette_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "bike", "biking"})) {
      iconId = R.drawable.ic_directions_bike_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "picture", "photo", "camera", "instagram", "lightroom", "aperture"})) {
      iconId = R.drawable.ic_camera_alt_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "sun", "tan"})) {
      iconId = R.drawable.ic_wb_sunny_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "drinks", "alcohol", "beer", "shots", "vodka", "whiskey", "wine", "bar", "lounge", "club"})) {
      iconId = R.drawable.ic_local_bar_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "eat", "dinner", "lunch", "food", "takeout", "eat24", "pizza", "cooking", "recipe", "restaurant"})) {
      iconId = R.drawable.ic_local_dining_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "birthday", "celebration"})) {
      iconId = R.drawable.ic_cake_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "build", "fix"})) {
      iconId = R.drawable.ic_build_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "love", "favorite", "best"})) {
      iconId = R.drawable.ic_favorite_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "bought", "buy", "money", "pay"})) {
      iconId = R.drawable.ic_attach_money_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "home", "house", "rent"})) {
      iconId = R.drawable.ic_home_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "pizza"})) {
      iconId = R.drawable.ic_local_pizza_white_48dp;
    }
    return iconId;
  }

}
