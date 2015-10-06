# Immutable Object Graph Instantiation

[![Build Status](https://travis-ci.org/rafaeldff/Iogi.svg?branch=master)](https://travis-ci.org/rafaeldff/Iogi)
[![][maven img]][maven]
[![][license img]][license]

[maven]:http://search.maven.org/#search|gav|1|g:"br.com.caelum"%20AND%20a:"iogi"
[maven img]:https://maven-badges.herokuapp.com/maven-central/br.com.caelum/iogi/badge.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

## What is Iogi

Iogi is a small library for instantiating objects (and graphs therof) in Java.  It's primary motivation is to help with
unmarshalling request parameters in Web applications.

The project was born as a spin-off of the [VRaptor Web Framework](http://www.vraptor.org/).  Many frameworks use OGLN
to construct objects, but, as it's name atests, OGNL is most useful as a language for *navigating* object graphs.  As such,
it is oriented around properties as getter/setter pairs. Iogi, on the other hand, is able to instantiate objects through
constructor parameters or setters. Iogi also supports dependency injection, so you can inject arbitrary objects (perhaps with
the help of a DI framework) into your domain objects as they are instantiated.

## Sample code

Here are some sample classes that you might want to instantiate:

```java
public static class House {
    private Cat cat;

    public void setCat(final Cat cat) {
        this.cat = cat;
    }

    public Cat getCat() {
        return cat;
    }
}

public static class Cat {
    private Leg firstLeg;

    public void setFirstLeg(final Leg firstLeg) {
        this.firstLeg = firstLeg;
    }

    public Leg getFirstLeg() {
        return firstLeg;
    }
}

public static class Leg {
    private Integer id;

    public void setId(final Integer id) {
        this.id = id;
    }

}
```

And here is how you can instantiate them with Iogi:

```java
final Target<House> target = Target.create(House.class, "house");
final Parameter parameter = new Parameter("house.cat.firstLeg.id", "42");
final House house = iogi.instantiate(target, parameter);
assertThat(house.cat.firstLeg.id, is(equalTo(42)));
```

## Installing with Maven

Altough some old versions can be found in mvnrepository, Iogi is currently distributed
through sonatype repositories. So, if you are using maven, you can simply add the following
dependency to your `pom.xml`:

```xml
<dependency>
	<groupId>br.com.caelum</groupId>
	<artifactId>iogi</artifactId>
	<version>1.0.0</version>
</dependency>
```

And add sonatype repository:

```xml
<repository>
	<id>sonatype-oss-public</id>
	<url>http://oss.sonatype.org/content/groups/public/</url>
	<releases>
		<enabled>true</enabled>
	</releases>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
</repository>

```
