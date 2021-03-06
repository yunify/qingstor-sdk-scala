# QingStor SDK for Scala

[![Build Status](https://travis-ci.org/yunify/qingstor-sdk-scala.svg?branch=master)](https://travis-ci.org/yunify/qingstor-sdk-scala)
[![API Reference](http://img.shields.io/badge/api-reference-green.svg)](http://docs.qingcloud.com/qingstor/)
[![License](http://img.shields.io/badge/license-apache%20v2-blue.svg)](https://github.com/yunify/qingstor-sdk-scala/blob/master/LICENSE)

the official QingStor SDK for the Scala programing language

## Getting Started

### Installation

Refer to the [Installation Guide](https://github.com/yunify/qingstor-sdk-scala/blob/master/docs/installation.md), and have this SDK installed.

### Preparation

Before your start, please go to [QingCloud Console](https://console.qingcloud.com/access_keys/) 
to create a pair of QingCloud API AccessKey.

___API AccessKey Example:___

```yaml
access_key_id: 'ACCESS_KEY_ID_EXAMPLE'
secret_access_key: 'SECRET_ACCESS_KEY_EXAMPLE'
```

### Usage

Now you are ready to code. You can read the detailed guides in the list below to 
have a clear understanding or just take the quick start code example.

Checkout our [releases](https://github.com/yunify/qingstor-sdk-scala/releases) and 
[change log](https://github.com/yunify/qingstor-sdk-scala/blob/master/CHANGELOG.md) 
for information about the latest features, bug fixes and new ideas.

- [Configuration Guide](docs/configuration.md)
- [QingStor Service Usage Guide](docs/qingstor_service_usage.md)

___Quick Start Code Example:___
```scala
package demo

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.QingStor

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Demo extends App {
  val config = QSConfig("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")

  // Initialize service object for QingStor.
  val qsService = QingStor(config)

  // List all buckets.
  val outputFuture = qsService.listBuckets(QingStor.ListBucketsInput())
  val listBucketsOutput = Await.result(outputFuture, Duration.Inf)

  // Print HTTP status code.
  println(listBucketsOutput.statusCode.getOrElse(-1))

  // Print the count of buckets.
  println(listBucketsOutput.count.getOrElse(-1))

  // Print the first bucket name.
  println(listBucketsOutput.buckets.flatMap(_.head.name).getOrElse("No buckets"))
  
  System.exit(0)
}
```

## Reference Documentations

- [QingStor Documentation](https://docs.qingcloud.com/qingstor/index.html)
- [QingStor Guide](https://docs.qingcloud.com/qingstor/guide/index.html)
- [QingStor APIs](https://docs.qingcloud.com/qingstor/api/index.html)

## Contributing

1. Fork it ( https://github.com/yunify/qingstor-sdk-scala/fork )
2. Create your feature branch (`git checkout -b new-feature`)
3. Commit your changes (`git commit -asm 'Add some feature'`)
4. Push to the branch (`git push origin new-feature`)
5. Create a new Pull Request

## LICENSE

The Apache License (Version 2.0, January 2004).

## HISTORY

This project was originally developed by [Cheerx](https://github.com/cheerx) as a third-party QingStor SDK for Scala. Later on, it was accepted to be the official QingStor SDK for Scala by QingStor team. It will be maintained by both QingStor team and Cheerx.
