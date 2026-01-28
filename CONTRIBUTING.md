# Contributing to PromoPing Bot Suporter

> **Note:** This is the administrative Discord bot of the PromoPing ecosystem, developed in Java using JDA (Java Discord API).

Thank you for considering contributing to PromoPing Bot Suporter! The PromoPing community uses GitHub to receive contributions through issues and pull requests.

To report bugs or suggest improvements, use the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). Check if a similar issue already exists before creating a new one. For bugs, include a descriptive title, detailed description, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment information.

To contribute code, fork the repository, clone your fork, install dependencies (Maven/Gradle for Java), configure environment variables and the configuration file. Create a branch for your feature or fix, follow the project's code standards (Java following standard conventions, JDA for Discord), test your changes, and open a Pull Request with a clear description of the changes.

If you find a bug, check if it has already been reported in the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). If it hasn't been reported, create a new issue with a clear title, detailed description of the problem, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment details (OS, Java version, JDA version, etc.).

Suggestions are always welcome! Check if a similar suggestion already exists in the Issues. Create an issue with the `enhancement` or `feature request` tag, clearly describing the problem the improvement solves, how you imagine it would work, and the benefits for users.

## Contributing Code

### Development Environment Setup

To get started with contributing, first fork the repository and clone your fork locally to your machine. Once you have the project, install the dependencies using Maven or Gradle (check the build file in the project for details on how to proceed). You will need to configure the environment variables; to do this, create a `config.properties` file based on the available example (if there is one). Don’t forget to insert your Discord token into the configuration file so that the bot can run correctly.

### Development Process

Before implementing a new feature or fixing a bug, create a dedicated branch for your work, using a naming convention such as `feature/feature-name` or `fix/bug-name`. While developing, follow the project's code standards (Java following official conventions and best practices for JDA/Discord integration). Write clean code: keep functions small and focused, use descriptive names, add comments when necessary, and avoid duplicating logic. Manually test your changes to ensure nothing is broken after your implementation. Make commits using clear conventions such as: feat, fix, docs, style, refactor, test, or chore. Once finished, push your changes to your fork and open a Pull Request with a detailed description of the modifications made, linking issues if present, adding screenshots if necessary, and completing the checklist.

For more details about the collaboration process or to understand the PromoPing Bot Suporter guidelines, consult the [README.md](README.md) and submission instructions.

## Code Standards

When developing in Java, use descriptive names in camelCase for variables and methods, and PascalCase for class names. Keep indentation consistent (4 spaces or tabs, according to the project’s configuration). Add JavaDoc to public classes and methods, prefer interfaces when appropriate, and always handle exceptions properly. Functions should be small, focused, and fulfill only one responsibility.

For JDA (Java Discord API), follow the best practices recommended by the library. Prefer asynchronous event handling when appropriate, validate permissions before executing administrative commands, and use proper logging for debugging and auditing.

## Pull Request Checklist

Before submitting a Pull Request, ensure your code adheres to project standards, all functionalities have been manually tested, and there are no compilation or lint errors. Update documentation if necessary. Commits should follow the defined conventions, and the branch must be up-to-date with the main/master branch. The Pull Request should contain a clear description and illustrative screenshots if applicable, and confirm that bot commands have been tested on Discord when necessary.

## Review Process

Maintainers will review your PR by checking if the code follows standards, testing the changes, and suggesting improvements if necessary. Respond to comments, make requested changes, and update the PR as needed. After approval, the PR will be merged and you will be credited as a contributor.

## Questions?

If you have questions about how to contribute, open an issue with the `question` tag or contact us at <corporation.promoping@gmail.com>

Your friendly PromoPing community!
Thank you for contributing to make PromoPing Bot Suporter better!
