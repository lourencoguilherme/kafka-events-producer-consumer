package com.guimtlo.learnkafka.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.guimtlo.learnkafka.domains.Book;
import com.guimtlo.learnkafka.domains.LibraryEvent;
import com.guimtlo.learnkafka.domains.enums.TypeEvent;

public class LibraryEventFixture implements TemplateLoader {

    public  static final String LIBRARY_EVENT = "LIBRARY_EVENT";

    @Override
    public void load() {
        Fixture.of(LibraryEvent.class).addTemplate(LIBRARY_EVENT, new Rule(){{
            add("libraryEvent", random(Integer.class, 1,2,3,4,5,6,7,8,9));
            add("typeEvent", random(TypeEvent.class, TypeEvent.values()));
            add("book", one(Book.class, BookFixture.BOOK) );
        }});
    }
}
