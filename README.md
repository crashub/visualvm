# How to build CRaSH visual plugin

## Obtaining CRaSH source code

CRaSH visual plugin can be obtained by cloning the Git repository `git@github.com:crashub/visualvm.git`

<pre><code>git clone git@github.com:crashub/visualvm.git</code></pre>

## Building CRaSH visualvm plugin

CRaSH plugin are built with Maven.

First you need to add two repositories:

<pre><code>
<profile>
  <id>crash-visualvm-repositories</id>
  <repositories>
    <repository>
      <id>netbeans-repository</id>
      <name>repository hosting netbeans.org api artifacts</name>
      <url>http://bits.netbeans.org/maven2</url>
    </repository>
    <repository>
      <id>exo-repository</id>
      <name>repository hosting exoplatform.org 3rd party</name>
      <url>http://repository.exoplatform.org/content/repositories/thirdparty</url>
    </repository>
  </repositories>
</profile>
</code></pre>

Then build the maven project:

<pre><code>mvn clean install -Pcrash-visualvm-repositories</code></pre>

it will produce:

- `plugin/target/crash-visualvm-plugin-<version>.nbm`

## Deploy visualvm plugin

The plugin repository is github pages, to deploy it:

<pre><code>mvn nbm:autoupdate -Pcrash-visualvm-repositories
git checkout gh-pages
cp target/netbeans_site/* plugin/
git add plugin/crash-visualvm-plugin* plugin/updates.*
git commit -a -m "Deploy new plugin version"
git push origin gh-pages
</code></pre>
