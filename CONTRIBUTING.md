# Contributing to PromoPing Bot Suporter

> **Note:** This is the administrative Discord bot of the PromoPing ecosystem, developed in Java using JDA (Java Discord API).

Thank you for considering contributing to PromoPing Bot Suporter! The PromoPing community uses GitHub to receive contributions through issues and pull requests.

To report bugs or suggest improvements, use the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). Check if a similar issue already exists before creating a new one. For bugs, include a descriptive title, detailed description, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment information.

To contribute code, fork the repository, clone your fork, install dependencies (Maven/Gradle for Java), configure environment variables and the configuration file. Create a branch for your feature or fix, follow the project's code standards (Java following standard conventions, JDA for Discord), test your changes, and open a Pull Request with a clear description of the changes.

If you find a bug, check if it has already been reported in the [GitHub Issues](https://github.com/juliareboucasleite/PromoPing/issues). If it hasn't been reported, create a new issue with a clear title, detailed description of the problem, steps to reproduce, expected vs. current behaviour, screenshots (if applicable), and environment details (OS, Java version, JDA version, etc.).

Suggestions are always welcome! Check if a similar suggestion already exists in the Issues. Create an issue with the `enhancement` or `feature request` tag, clearly describing the problem the improvement solves, how you imagine it would work, and the benefits for users.

## Contributing Code

### Development Environment Setup

1. Fork the repository
2. Clone your fork locally
3. Install dependencies using Maven or Gradle (check the project's build file)
4. Configure environment variables by creating a `config.properties` file based on the example (if it exists)
5. Configure the Discord token in the configuration file

### Development Process

1. Create a branch for your feature/fix (`feature/feature-name` or `fix/bug-name`)
2. Follow code standards (Java following standard conventions, JDA for Discord)
3. Write clean code (small, focused functions, descriptive names, comments when necessary, avoid duplication)
4. Test your changes manually and verify you haven't broken existing functionality
5. Make commits following the convention (feat, fix, docs, style, refactor, test, chore)
6. Push to your fork
7. Open a Pull Request with a clear description of the changes, related issues (if any), screenshots (if applicable), and verification checklist

To better understand how the PromoPing Bot Suporter project is managed and how to collaborate, we recommend reviewing [README.md](README.md) and the submission guidelines.

## Code Standards

**Java**:
- Use descriptive names in camelCase for variables and methods
- Use PascalCase for classes
- Follow standard Java conventions
- Use consistent indentation (4 spaces or tabs as configured in the project)
- Add JavaDoc for public classes and methods
- Prefer interfaces when appropriate
- Use proper exception handling
- Keep methods small and focused on a single responsibility

**JDA (Java Discord API)**:
- Follow JDA library best practices
- Handle events asynchronously when appropriate
- Validate permissions before executing administrative commands
- Use adequate logging for debugging and auditing

## Pull Request Checklist

Before submitting a PR, verify:
- [ ] Code follows project standards
- [ ] Functionality tested manually
- [ ] No compilation or lint errors
- [ ] Documentation updated (if necessary)
- [ ] Commits follow the convention
- [ ] Branch is up to date with main/master
- [ ] PR has a clear description and screenshots (if applicable)
- [ ] Bot commands were tested on Discord (if applicable)

## Review Process

Maintainers will review your PR by checking if the code follows standards, testing the changes, and suggesting improvements if necessary. Respond to comments, make requested changes, and update the PR as needed. After approval, the PR will be merged and you will be credited as a contributor.

## Questions?

If you have questions about how to contribute, open an issue with the `question` tag or contact us at <corporation.promoping@gmail.com>

Your friendly PromoPing community!
Thank you for contributing to make PromoPing Bot Suporter better!
