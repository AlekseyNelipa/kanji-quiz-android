# JLPT Kanji Reading Quiz (Android)

An Android application for testing the knowledge of readings of Japanese words that include kanji.
The app presents a word and prompts the user to enter its reading, using either hiragana or romaji.

---

## Data source

This application uses vocabulary data derived from the **open-anki-jlpt-decks**:

https://github.com/jamsinclair/open-anki-jlpt-decks

Author: jamsinclair  
License: Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)  
License text: https://creativecommons.org/licenses/by-sa/4.0/

See `NOTICE.md` for full attribution details.

---

## Data usage and modifications

The original JLPT word list has been modified for use as a reading quiz dataset.
The following transformations were applied:

- Entries without kanji were removed
- Entries with multiple possible readings were removed
- For verb entries, a trailing `する` was removed from the reading where appropriate

The resulting dataset is used to generate quiz questions where the kanji form
is shown and the user must input the correct reading.

These modifications constitute an adaptation of the original dataset.

---

## Licensing note

The application source code is licensed separately.

Only the JLPT vocabulary data and any derivatives of that data are licensed
under Creative Commons Attribution-ShareAlike 4.0 (CC BY-SA 4.0).
