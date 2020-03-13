package mops.authdemo;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    private String attribute1;
    private String attribute2;
    private String attribute3;

    public static List<Entry> generate(int n) {
        final Faker faker = new Faker(Locale.GERMAN);
        return IntStream.range(0, 15).mapToObj(
                value -> new Entry(
                        faker.shakespeare().romeoAndJulietQuote(),
                        faker.shakespeare().asYouLikeItQuote(),
                        faker.shakespeare().hamletQuote())
        ).collect(Collectors.toList());
    }
}
