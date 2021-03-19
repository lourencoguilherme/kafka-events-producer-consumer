package com.guimtlo.learnkafka.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.guimtlo.learnkafka.domains.Book;
import com.guimtlo.learnkafka.domains.LibraryEvent;
import com.guimtlo.learnkafka.domains.enums.TypeEvent;

public class BookFixture implements TemplateLoader {

    public  static final String BOOK = "BOOK";

    @Override
    public void load() {
        Fixture.of(Book.class).addTemplate(BOOK, new Rule(){{
            add("bookId", random(Integer.class, 1,2,3,4,5,6,7,8,9));
            add("bookName", regex("[A-Za-z]{1,9}"));
            add("bookAuthor", regex("[A-Za-z]{1,9}"));
        }});
    }
}
