#include <algorithm>
#include <map>
#include <regex>
#include "iostream"
#include "vector"

using namespace ::std;
char SENTINEL = '$';

map<string, string> classes = {
        {"word",   "noun"},
        {"string", "adjective"},
        {" ",      "seperator"},
        {",",      "seperator"}
};

class Tokenizer {
public:

    string get_map(char &key) {
        if (classes[string(1, key)].empty()) {
            if (isalpha(key))
                return "char";
            else if (isdigit(key))
                return "digit";
            else
                return "seperator";
        } else {
            return classes[string(1, key)];
        }
    }

    vector<string> get_tokens(string message) {
        int n = static_cast<int>(message.size());
        int lexeme_begin = 0;
        int forward = 0;
        vector<string> tokens;
        string tok;
        char token_type = '-';
        bool sentinel_found = false;

        // grammar definition
        regex noun("\\w+");                 // zero or more alphabets.
        regex keyword("if|then");           // either if or else.
        regex separator("\\.+|\\s+|,|, ");  // some common separators.
        regex verb("hate|like");            // either hate or like.
        // grammar definition end.

        while (lexeme_begin < n && message[forward] != SENTINEL) {
            string begin_class = get_map(message[lexeme_begin]);

            forward = lexeme_begin;
            while (get_map(message[forward]) == begin_class && forward < n) {
                forward += 1;
                if (message[forward] == SENTINEL) {
                    sentinel_found = true;
                    break;
                }
            }
            if (!sentinel_found) {
                tok = message.substr(static_cast<unsigned long>(lexeme_begin),
                                     static_cast<unsigned long>(forward - lexeme_begin));

                if (regex_match(tok, keyword)) token_type = 'K';
                else if (regex_match(tok, noun)) token_type = 'N';
                else if (regex_match(tok, separator)) token_type = 'S';
                else if (regex_match(tok, verb)) token_type = 'V';

                tokens.push_back("<'" + tok + "', " + token_type + ">");
                lexeme_begin = forward;
                forward -= 1;
            }
        }
        if (message[forward] != SENTINEL) {
            cout << "SyntaxError: EOL while scanning string literal.";
            exit(0);
        }
        return tokens;
    }
};


string input(const string &message = "Enter the message: ") {
    string line;

    // flush to prevent endl.
    cout << message << flush;
    getline(cin, line);

    return line;
}

int main() {
    string message = input();

    auto *tokenizer = new Tokenizer();

    vector<string> tokens = tokenizer->get_tokens(message);

    cout << "TOKENS: ";
    for (const auto &token: tokens) {
        cout << token << ", ";
    }
    cout << endl << "Number of tokens: " << tokens.size();
    return 0;
}



/*
 * This is the sample imput given by Shobha mam:
 * "if dogs hate cats then they bark. if cats love milk then they drink. $"
 *
 */